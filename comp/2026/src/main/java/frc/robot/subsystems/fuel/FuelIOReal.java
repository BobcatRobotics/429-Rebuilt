package frc.robot.subsystems.fuel;

import static frc.robot.Constants.IntakeConstants.FEEDER_MOTOR_ID;
import static frc.robot.Constants.IntakeConstants.SHOOTER_INTAKE_MOTOR_ID;
import static frc.robot.Constants.IntakeConstants.SHOOTER_MOTOR_ID;
import static frc.robot.Constants.IntakeConstants.FEEDER_MOTOR_CURRENT_LIMIT;
import static frc.robot.Constants.IntakeConstants.SHOOTER_MOTOR_CURRENT_LIMIT;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.StrictFollower;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;


public class FuelIOReal implements FuelIO {

    private TalonFX feedMotor;
    private TalonFX shooterMotor;
    private TalonFX shooterIntakeMotor;
    private DutyCycleOut shooterMotorRequest = new DutyCycleOut(0);
  private DutyCycleOut feederMotorRequest = new DutyCycleOut(0);
    public void updateInputs(FuelIOInputs inputs) {

    }

    /**
     * Set up the Left Intake Launcher motor,
     * The configuration MUST set up and have the following configurations ; stator
     * current limit, neutral mode , inverted
     */
    public void configureShooter() {
        shooterMotor = new TalonFX(SHOOTER_MOTOR_ID, new CANBus("rio"));
        var shooterConfigure = new TalonFXConfiguration();
        shooterConfigure.MotorOutput.NeutralMode = NeutralModeValue.Coast;
        shooterConfigure.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
        shooterConfigure.CurrentLimits.StatorCurrentLimit = SHOOTER_MOTOR_CURRENT_LIMIT;
        shooterConfigure.CurrentLimits.StatorCurrentLimitEnable = true;
        shooterMotor.getConfigurator().apply(shooterConfigure);
        shooterMotor.setControl(new StrictFollower(SHOOTER_INTAKE_MOTOR_ID));
    }

    /**
     * Set up the Right Intake Launcher motor,
     * The configuration MUST set up and have the following configurations ; stator
     * current limit, neutral mode , inverted
     */
    public void configureShooterIntake() {
        shooterIntakeMotor = new TalonFX(SHOOTER_INTAKE_MOTOR_ID, new CANBus("rio"));
        var shooterIntakeConfigure = new TalonFXConfiguration();
      shooterIntakeConfigure.MotorOutput.NeutralMode = NeutralModeValue.Coast;
      shooterIntakeConfigure.MotorOutput.Inverted = InvertedValue.CounterClockwise_Positive;
      shooterIntakeConfigure.CurrentLimits.StatorCurrentLimit = SHOOTER_MOTOR_CURRENT_LIMIT;
      shooterIntakeConfigure.CurrentLimits.StatorCurrentLimitEnable = true;
        shooterIntakeMotor.getConfigurator().apply(shooterIntakeConfigure);
    }

    /**
     * Set up the Indexer motor,
     * The configuration MUST set up and have the following configurations ; stator
     * current limit
     */
    public void configureFeeder() {
        feedMotor = new TalonFX(FEEDER_MOTOR_ID, new CANBus("rio"));
        var feederConfigure = new TalonFXConfiguration();
        feederConfigure.MotorOutput.NeutralMode = NeutralModeValue.Coast;
        feederConfigure.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
        feederConfigure.CurrentLimits.StatorCurrentLimit = FEEDER_MOTOR_CURRENT_LIMIT;
        feederConfigure.CurrentLimits.StatorCurrentLimitEnable = true;
        feedMotor.getConfigurator().apply(feederConfigure);
    }

    /**
     * Sets the velocity of the intake Launcher.
     * This is PID based and velocityvoltage control mode.
     * This is more advanced and must be completed after manual control has been succesfully implemented.
     * 
     * 
     */
    public void setShooterIntakePower(double power) {
        shooterIntakeMotor.setControl(shooterMotorRequest.withOutput(power)); // positive for shooting
    }

    /**
     * Sets the speed of the intake Launcher.
     * This is not PID based and a duty cycle output control mode.
     * Start with this function before doing the setIntakeLauncherRollerVelocity.
     * 
     * @param power
     */
    public void setFeederPower(double power) {
        feedMotor.setControl(feederMotorRequest.withOutput(power)); // positive for shooting
}

    /**
     * Sets the velocity of the intake Launcher.
     * This is PID based and velocityvoltage control mode.
     * This is more advanced and must be completed after manual control has been succesfully implemented.
     * 
     * @param velSetpoint
     */
    public void setFeederRollerVelocity(double velSetpoint) {

    }

    /**
     * Sets the speed of the intake Launcher.
     * This is not PID based and a duty cycle output control mode.
     * Start with this function before doing the setFeederRollerVelocity.
     * 
     * @param power
     */
    public void setFeederRoller(double power) {
        feedMotor.setControl(feederMotorRequest.withOutput(power)); // positive for shooting
    }

    public void stop() {
        feedMotor.stopMotor();
        shooterIntakeMotor.stopMotor();
    }
}
