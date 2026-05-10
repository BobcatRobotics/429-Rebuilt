package frc.robot.subsystems.led;

import static edu.wpi.first.units.Units.Seconds;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.AddressableLEDBufferView;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.LedConstants;
import frc.robot.RobotState;

public class Led extends SubsystemBase {
  private static final int kPort = LedConstants.LED_PORT;
  private static final int kLength = LedConstants.LED_LENGTH;

  private final AddressableLED m_led;
  private final AddressableLEDBuffer m_buffer;

  private final AddressableLEDBufferView m_top;
  private final AddressableLEDBufferView m_bottom;

  private final LEDPattern offPattern = LEDPattern.solid(Color.kBlack);

  public Led() {
    m_led = new AddressableLED(kPort);
    m_buffer = new AddressableLEDBuffer(kLength);
    m_led.setLength(kLength);
    
    m_top = m_buffer.createView(LedConstants.LED_START_TOP, LedConstants.LED_END_TOP);
    m_bottom = m_buffer.createView(LedConstants.LED_START_BOTTOM, LedConstants.LED_END_BOTTOM);

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
    else if (RobotState.getInstance().getIsBlockerDeployed())
        setBlockerDeployed();
    else
        turnOffTop();

    // Periodically send the latest LED color data to the LED strip for it to display
    m_led.setData(m_buffer);
  }

  private void turnOffTop() {
    offPattern.applyTo(m_top);
  }

  private void turnOffBottom() {
    offPattern.applyTo(m_bottom);
  }

  private void setAutoDriving()  {    
    LEDPattern autoDrivingBlinkingPattern = LEDPattern.solid(Color.kYellow).blink(Seconds.of(0.25), Seconds.of(0.25));
    autoDrivingBlinkingPattern.applyTo(m_top);
  }

  private void setAligned() {
    LEDPattern alignedSolidColorPattern = LEDPattern.solid(Color.kBlue);
    alignedSolidColorPattern.applyTo(m_top);
  }

  private void setSeekingAlign() {
    LEDPattern alignedSolidColorPattern = LEDPattern.solid(Color.kWhite);
    alignedSolidColorPattern.applyTo(m_top);
  }

  private void setBlockerDeployed() {
    LEDPattern blockerDeployedBlinkingPattern = LEDPattern.solid(Color.kOrange).blink(Seconds.of(2), Seconds.of(0.5));
    blockerDeployedBlinkingPattern.applyTo(m_top);
  }

  public void setAllianceColor() {
    LEDPattern allianceColor = LEDPattern.solid(RobotState.getInstance().getAlliance() == Alliance.Red ? Color.kRed: Color.kBlue);
    allianceColor.applyTo(m_bottom);
  }

  public void enableAutoriveMode(){
    LEDPattern base = LEDPattern.solid(Color.kYellow);

    LEDPattern pattern = base.blink(Seconds.of(2));

    pattern.applyTo(m_top);
  }
  
  public void disableAutoDriveMode(){
    LEDPattern base = LEDPattern.solid(Color.kBlack);

    base.applyTo(m_top);
  }
}