package frc.robot.subsystems.climber;

import static frc.robot.Constants.ClimbConstants.CLIMBER_CLIMBED;
import static frc.robot.Constants.ClimbConstants.CLIMBER_MOTOR_CURRENT_LIMIT;
import static frc.robot.Constants.ClimbConstants.CLIMBER_MOTOR_ID;
import static frc.robot.Constants.ClimbConstants.CLIMBER_PRECLIMB;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.SoftwareLimitSwitchConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.motorcontrol.Talon;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.Constants.ClimbConstants;

public class ClimberIOReal implements ClimberIO {
    private DutyCycleOut climberMotorrequest = new DutyCycleOut(0).withEnableFOC(false);
    private TalonFX climberMotor;
    private TalonFXConfiguration ClimberConfig = new TalonFXConfiguration();
    private Slot0Configs climberSlot0Config;
    private final PositionVoltage postionVoltageRequest = new PositionVoltage(0).withEnableFOC(false);

    public void updateInputs(ClimberIOInputs inputs) {

    }

    /**
     * Set up the Left Intake Launcher motor,
     * The configuration MUST set up and have the following configurations ; stator
     * current limit, neutral mode , inverted state
     */
    public void configureClimber() {
        climberMotor = new TalonFX(CLIMBER_MOTOR_ID, new CANBus("rio"));

    
        ClimberConfig.MotorOutput.NeutralMode = NeutralModeValue.Brake;
        ClimberConfig.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
        ClimberConfig.CurrentLimits.SupplyCurrentLimit = CLIMBER_MOTOR_CURRENT_LIMIT;
        ClimberConfig.CurrentLimits.SupplyCurrentLimitEnable = true;
        ClimberConfig.CurrentLimits.StatorCurrentLimit = CLIMBER_MOTOR_CURRENT_LIMIT;
        ClimberConfig.CurrentLimits.StatorCurrentLimitEnable = true;
        ClimberConfig.SoftwareLimitSwitch.ForwardSoftLimitThreshold = CLIMBER_CLIMBED;
        ClimberConfig.SoftwareLimitSwitch.ForwardSoftLimitEnable = true;
        ClimberConfig.SoftwareLimitSwitch.ReverseSoftLimitThreshold = CLIMBER_PRECLIMB;
        ClimberConfig.SoftwareLimitSwitch.ReverseSoftLimitEnable = true;
        ClimberConfig.Feedback.SensorToMechanismRatio = 900;

        climberMotor.getConfigurator().apply(ClimberConfig);

        climberSlot0Config = new Slot0Configs();
        climberSlot0Config.kP = ClimbConstants.kClimbMotorkP;
        climberSlot0Config.kI = 0;
        climberSlot0Config.kD = 0;
        climberSlot0Config.kV = ClimbConstants.kClimbMotorkV;
        climberMotor.getConfigurator().apply(climberSlot0Config);

    }

    // public Command disableLimits(){

    //     if (DriverStation.isFMSAttached())        
    //         return Commands.none();

    //     return Commands.run(() -> climberMotor.getConfigurator().apply(ClimberConfig.withSoftwareLimitSwitch(
    //         new SoftwareLimitSwitchConfigs()
    //         .withForwardSoftLimitEnable(false)
    //         .withReverseSoftLimitEnable(false))));        
    // }

    // public Command enableLimits(){

    //     if (DriverStation.isFMSAttached())        
    //         return Commands.none();

    //     return Commands.run(() -> climberMotor.getConfigurator().apply(ClimberConfig.withSoftwareLimitSwitch(
    //         new SoftwareLimitSwitchConfigs()
    //         .withForwardSoftLimitEnable(true)
    //         .withReverseSoftLimitEnable(true))));        
    // }


    /**
     * Sets the position of the climber.
     * This is PID based and position control mode.
     * 
     * @param angleSetPoint
     */
    public void setClimberPosition(double angleSetPoint) {
        climberMotor.setControl(postionVoltageRequest.withPosition(angleSetPoint));
    }

    public void setClimberZero() {
        climberMotor.setPosition(0);
    }

    public void setClimberBlockerSoftLimit(boolean isBlockerInstalled) {
        climberMotor.getConfigurator().apply(ClimberConfig.withSoftwareLimitSwitch(
             new SoftwareLimitSwitchConfigs()
             .withReverseSoftLimitThreshold(ClimbConstants.BLOCKER_INSTALLED_SOFT_LIMIT)));
    }

    /**
     * Sets the output of the climber.
     * This is not PID based and will apply output to the motor.
     * 
     */
    public void setClimberPower(double power) {
        climberMotor.setControl(climberMotorrequest.withOutput(power));
    }

    public void stop() {
        setClimberPower(ClimbConstants.CLIMBER_STOP);
        climberMotor.stopMotor();
    }

    public StatusSignal<Angle> getClimberMotorPosition() {
        return climberMotor.getPosition();
    }
}
