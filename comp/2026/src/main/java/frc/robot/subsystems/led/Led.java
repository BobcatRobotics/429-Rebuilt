package frc.robot.subsystems.led;

import static edu.wpi.first.units.Units.Percent;
import static edu.wpi.first.units.Units.Seconds;

import java.net.PortUnreachableException;
import java.util.Optional;

import com.ctre.phoenix6.configs.LEDConfigs;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.AddressableLEDBufferView;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.LedConstants;
import frc.robot.RobotState;

public class Led extends SubsystemBase {
  private final AddressableLED m_led;
  private final AddressableLEDBuffer m_buffer;

  private final AddressableLEDBufferView m_top;
  private final AddressableLEDBufferView m_bottom;
  private final AddressableLEDBufferView m_wonAutoStart;
  private final AddressableLEDBufferView m_wonAutoEnd;
  public boolean halfRumbleStatus = false;
  public boolean fullRumbleStatus = false;
  public double rumbleTimer = Double.MAX_VALUE;


  public final LEDPattern offPattern = LEDPattern.solid(Color.kBlack);

  private final int wonAutoStartLastIndex = LedConstants.LED_START_BOTTOM + LedConstants.LED_WON_AUTO_COUNT;
  private final int wonAutoEndLastIndex = LedConstants.LED_END_BOTTOM -  LedConstants.LED_WON_AUTO_COUNT;
  
  public Led() {
    m_led = new AddressableLED(LedConstants.LED_PORT);
    m_buffer = new AddressableLEDBuffer(LedConstants.LED_LENGTH);
    m_led.setLength(LedConstants.LED_LENGTH);
    
    m_top = m_buffer.createView(LedConstants.LED_START_TOP, LedConstants.LED_END_TOP);
    m_bottom = m_buffer.createView(LedConstants.LED_START_BOTTOM, LedConstants.LED_END_BOTTOM);
    m_wonAutoStart = m_buffer.createView(LedConstants.LED_START_BOTTOM, wonAutoStartLastIndex);
    m_wonAutoEnd = m_buffer.createView(LedConstants.LED_END_BOTTOM, wonAutoEndLastIndex);
    
    turnOffTop();
    turnOffBottom();

    m_led.start();   
  }

  /*
     TOP STRIP ROBOT STATE
     1) Auto climbing (P1)
            Flashing yellow - anything where the robot is driving to a location on its own        
     2) Auto Align (P2)
            White solid - Seeking hub 
            Blue solid - Aligned to hub 
     3) Blocker deployed (P2)
            Pulsing orange - not 1hz (RSL)
    
     BOTOM STRIP FIELD STATE
     1) Our Scoring Shift (P1)
            Red/Blue Solid - Alliance color during our shift (starting one second prior to shift)
            Reducing LED from outside to inside - Start countdown to next shift at 10 seconds left in the shift
     2) Opponent scoring shift (P2)
            Off Solid - Other Alliance shift
            Red/Blue Blinking  - 7 seconds before Scoring Shift begins
     3) Transitional (P3)
            Green Solid outside - Won Auto
            Reducing LED from outside to inside - start countdown at 5 seconds left, only if won auto
   */
  @Override
  public void periodic() {

    if (RobotState.getInstance().getIsAutoDriving())
        setAutoDriving();
    else if (RobotState.getInstance().getIsAutoAligning()) {
        if (RobotState.getInstance().getIsAligned())
            setAligned();
        else 
            setSeekingAlign();
    }
    else
        turnOffTop();
    if (RobotState.getInstance().getIsBlockerDeployed())
    {
        setBlockerDeployed();
    }

    handleAllianceShift();
    setNearBrownout();

    // Periodically send the latest LED color data to the LED strip for it to display
    m_led.setData(m_buffer);
  }

  public void turnOffTop() {
    offPattern.applyTo(m_top);
  }

  private void turnOffBottom() {
    RobotState.getInstance().setIsAllianceShiftActive(false);
    offPattern.applyTo(m_bottom);
  }

  private void setAutoDriving()  {    
    LEDPattern autoDrivingBlinkingPattern = LEDPattern.solid(Color.kPurple)
        .blink(Seconds.of(0.25), Seconds.of(0.25))
        .atBrightness(Percent.of(LedConstants.LED_BRIGHTNESS_PERCENT));
    autoDrivingBlinkingPattern.applyTo(m_top);
  }

  private void setAligned() {
    LEDPattern alignedSolidColorPattern = LEDPattern.solid(Color.kBlue)
        .atBrightness(Percent.of(LedConstants.LED_BRIGHTNESS_PERCENT));
    alignedSolidColorPattern.applyTo(m_top);
  }

  private void setSeekingAlign() {
    LEDPattern alignedSolidColorPattern = LEDPattern.solid(Color.kWhite)
        .atBrightness(Percent.of(LedConstants.LED_BRIGHTNESS_PERCENT));
    alignedSolidColorPattern.applyTo(m_top);
  }

  private void setBlockerDeployed() {
    LEDPattern blockerDeployedBlinkingPattern = LEDPattern.solid(Color.kYellow)
        .blink(Seconds.of(1.5), Seconds.of(0.5))
        .atBrightness(Percent.of(LedConstants.LED_BRIGHTNESS_PERCENT));
    blockerDeployedBlinkingPattern.applyTo(m_top);
  }

  private void handleAllianceShift() {
    Alliance alliance = RobotState.getInstance().getAlliance();

    if (DriverStation.isAutonomousEnabled()) {
        turnOffBottom();
        return;
    }

    if (DriverStation.isTeleopEnabled()) { 
        // We're teleop enabled, compute.
        String gameData = DriverStation.getGameSpecificMessage();
        // If we have no game data, we cannot compute, assume hub is active, as its likely early in teleop.
        if (gameData.isEmpty()) {
            setAllianceHubActive();
            return;
        }

        boolean redInactiveFirst = false;
        switch (gameData.charAt(0)) {
            case 'R' -> redInactiveFirst = true;
            case 'B' -> redInactiveFirst = false;
            default -> {
            // If we have invalid game data, assume hub is active.
                setAllianceHubActive();
                return;
            }
        }
        
        double matchTime = DriverStation.getMatchTime();

        // Shift was is active for blue if red won auto, or red if blue won auto.
        boolean shift1Active = alliance == Alliance.Red ? !redInactiveFirst : redInactiveFirst;

         if (matchTime > LedConstants.END_TRANSITION_PERIOD_SECONDS) {
             // Transition shift, hub is active.
            setAllianceHubActive();
            if (!shift1Active) {
                setWonAuto();
                countdown(wonAutoStartLastIndex, wonAutoEndLastIndex - 1, LedConstants.TRANSITION_COUNTDOWN_SECONDS, matchTime, LedConstants.END_TRANSITION_PERIOD_SECONDS);
            }
            return;
        } 
        // Shift 1
        else if (matchTime > LedConstants.END_FIRST_SHIFT_SECONDS) {
           // if shift 1 is active, set alliance color full and begin countdown logic for end of shift
            if (shift1Active) {
                setAllianceHubActive();
                countdown(LedConstants.LED_START_BOTTOM-1, LedConstants.LED_END_BOTTOM, LedConstants.SHIFT_COUNTDOWN_SECONDS, matchTime, LedConstants.END_FIRST_SHIFT_SECONDS);
            }
            //if not active, begin 7 second shift start warning
            else if (matchTime < LedConstants.END_FIRST_SHIFT_SECONDS + LedConstants.SHIFT_START_WARNING_SECONDS && matchTime > LedConstants.END_FIRST_SHIFT_SECONDS + LedConstants.SHIFT_START_IMMINENT_SECONDS)
                setShiftStartWarning();
            //if not active and less than 3 seconds, begin shift imminent warning
            else if (matchTime < LedConstants.END_FIRST_SHIFT_SECONDS + LedConstants.SHIFT_START_IMMINENT_SECONDS){
                setShiftImminentWarning();
                rumbleTimer = matchTime - LedConstants.END_FIRST_SHIFT_SECONDS;
            }
            //turn off LEDs
            else {
                turnOffBottom();
                rumbleTimer = Double.MAX_VALUE;
            }
            return;
        } 
        else if (matchTime > LedConstants.END_SECOND_SHIFT_SECONDS) {
            // Shift 2
            if (shift1Active) {
                if (matchTime < LedConstants.END_SECOND_SHIFT_SECONDS + LedConstants.SHIFT_START_WARNING_SECONDS && matchTime > LedConstants.END_SECOND_SHIFT_SECONDS + LedConstants.SHIFT_START_IMMINENT_SECONDS)
                    setShiftStartWarning();
                else if (matchTime < LedConstants.END_SECOND_SHIFT_SECONDS + LedConstants.SHIFT_START_IMMINENT_SECONDS){
                    setShiftImminentWarning();
                    rumbleTimer = matchTime - LedConstants.END_FIRST_SHIFT_SECONDS;
                }
                else {
                    turnOffBottom();
                    rumbleTimer = Double.MAX_VALUE;
                }
            }
            else {
                setAllianceHubActive();
                countdown(LedConstants.LED_START_BOTTOM-1, LedConstants.LED_END_BOTTOM, LedConstants.SHIFT_COUNTDOWN_SECONDS, matchTime, LedConstants.END_SECOND_SHIFT_SECONDS);
            }
            return;
        } 
        else if (matchTime > LedConstants.END_THIRD_SHIFT_SECONDS) {
            // Shift 3
            if (shift1Active) {
                setAllianceHubActive();
                countdown(LedConstants.LED_START_BOTTOM-1, LedConstants.LED_END_BOTTOM, LedConstants.SHIFT_COUNTDOWN_SECONDS, matchTime, LedConstants.END_THIRD_SHIFT_SECONDS);
            }
            else if (matchTime < LedConstants.END_THIRD_SHIFT_SECONDS + LedConstants.SHIFT_START_WARNING_SECONDS && matchTime > LedConstants.END_THIRD_SHIFT_SECONDS + LedConstants.SHIFT_START_IMMINENT_SECONDS)
                setShiftStartWarning();
            else if (matchTime < LedConstants.END_THIRD_SHIFT_SECONDS + LedConstants.SHIFT_START_IMMINENT_SECONDS){
                setShiftImminentWarning();
                rumbleTimer = matchTime - LedConstants.END_FIRST_SHIFT_SECONDS;
            }
            else {
                turnOffBottom();
                rumbleTimer = Double.MAX_VALUE;
            }
            return;
        } 
        else if (matchTime > LedConstants.END_FOURTH_SHIFT_SECONDS) {
            // Shift 4
            if (shift1Active)
                if (matchTime < LedConstants.END_FOURTH_SHIFT_SECONDS + LedConstants.SHIFT_START_WARNING_SECONDS && matchTime > LedConstants.END_FOURTH_SHIFT_SECONDS + LedConstants.SHIFT_START_IMMINENT_SECONDS)
                    setShiftStartWarning();
                else if (matchTime < LedConstants.END_FOURTH_SHIFT_SECONDS + LedConstants.SHIFT_START_IMMINENT_SECONDS){
                    setShiftImminentWarning();
                    rumbleTimer = matchTime - LedConstants.END_FIRST_SHIFT_SECONDS;
                }
                else {
                    turnOffBottom();
                    rumbleTimer = Double.MAX_VALUE;
                }
            else 
                setAllianceHubActive();
            return;
        } 
        else {
            // End game, hub always active.
            setAllianceHubActive();
            countdown(LedConstants.LED_START_BOTTOM-1, LedConstants.LED_END_BOTTOM, LedConstants.ENDGAME_COUNTDOWN_SECONDS, matchTime, 0);
            return;
        }
    }
  }

  private void setAllianceHubActive() {
    RobotState.getInstance().setIsAllianceShiftActive(true);
    LEDPattern allianceColorSolidPattern = LEDPattern.solid(RobotState.getInstance().getAlliance() == Alliance.Red ? Color.kRed : Color.kBlue)
        .atBrightness(Percent.of(LedConstants.LED_BRIGHTNESS_PERCENT));
    allianceColorSolidPattern.applyTo(m_bottom);
  }

  private void setShiftStartWarning() {
    LEDPattern allianceColorSolidPattern = LEDPattern.solid(RobotState.getInstance().getAlliance() == Alliance.Red ? Color.kRed : Color.kBlue)
        .blink(Seconds.of(0.5), Seconds.of(0.5))
        .atBrightness(Percent.of(LedConstants.LED_BRIGHTNESS_PERCENT));
    allianceColorSolidPattern.applyTo(m_bottom);
  }

    private void setShiftImminentWarning() {
    LEDPattern allianceColorSolidPattern = LEDPattern.solid(RobotState.getInstance().getAlliance() == Alliance.Red ? Color.kRed : Color.kBlue)
        .blink(Seconds.of(0.25), Seconds.of(0.25))
        .atBrightness(Percent.of(LedConstants.LED_BRIGHTNESS_PERCENT));
    allianceColorSolidPattern.applyTo(m_bottom);
  }

  private void countdown(int countdownIndexStart, int countdownIndexEnd, int countDownSeconds, double matchTime, int periodEnd) {
        int diffInTime = (int)Math.ceil(matchTime - periodEnd);
        if (diffInTime > countDownSeconds)
            return;

        int numLeds = countdownIndexEnd - countdownIndexStart;
        numLeds -= numLeds % 2 == 0 ? 2 : 1;

        int elapsedSeconds = countDownSeconds - diffInTime;
        int numOneSideLeds = numLeds / 2 - elapsedSeconds;

        if (numOneSideLeds < diffInTime)
            return;

        int firstLedIndex = countdownIndexStart + elapsedSeconds + (numOneSideLeds > diffInTime ? numOneSideLeds - diffInTime : 0);
        
        for (int i = countdownIndexStart; i <= firstLedIndex; i++ )
            m_bottom.setRGB(i, 0, 0, 0);
            
        int lastLedIndex = countdownIndexEnd - elapsedSeconds - (numOneSideLeds > diffInTime ? numOneSideLeds - diffInTime : 0);

        for (int i = countdownIndexEnd; i >= lastLedIndex; i-- )
            m_bottom.setRGB(i, 0, 0, 0);
  }

  private void setWonAuto() {
    LEDPattern wonAutoSolidPattern = LEDPattern.solid(Color.kGreen)
        .atBrightness(Percent.of(LedConstants.LED_BRIGHTNESS_PERCENT));
    wonAutoSolidPattern.applyTo(m_wonAutoStart);
    wonAutoSolidPattern.applyTo(m_wonAutoEnd);
  }

  private void setNearBrownout() {
    LEDPattern nearBrownoutSolidPattern = LEDPattern.solid(Color.kRed);
    if(RobotController.getBrownoutVoltage() + 1 >= RobotController.getBatteryVoltage()) {
        nearBrownoutSolidPattern.applyTo(m_top);
    }
  }
}