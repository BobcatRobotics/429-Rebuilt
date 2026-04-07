package frc.robot.subsystems.fuel;

import static frc.robot.Constants.IntakeConstants.FEEDER_MOTOR_ID;
import static frc.robot.Constants.IntakeConstants.INTAKE_MOTOR_CURRENT_LIMIT;
import static frc.robot.Constants.ShooterConstants.SHOOTER_RIGHT_MOTOR_ID;
import static frc.robot.Constants.ShooterConstants.SHOOTER_LEFT_MOTOR_ID;
import static frc.robot.Constants.IntakeConstants.FEEDER_MOTOR_CURRENT_LIMIT;
import static frc.robot.Constants.ShooterConstants.SHOOTER_MOTOR_CURRENT_LIMIT;
import static frc.robot.Constants.IntakeConstants.INTAKE_MOTOR_ID;
import static frc.robot.Constants.IntakeConstants.INTAKE_MOTOR_SUPPLY_LIMIT;
//import static frc.robot.Constants.ShooterConstants.SHOOTER_MOTOR_SUPPLY_LIMIT;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.StrictFollower;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.units.measure.AngularVelocity;
import frc.robot.Constants.ShooterConstants;


public class FuelIOReal implements FuelIO {

    private TalonFX feedMotor;
    private TalonFX shooterMotorRight;
    private TalonFX shooterMotorLeft;
    private TalonFX intakeMotor;
    private DutyCycleOut shooterMotorRightRequest = new DutyCycleOut(0).withEnableFOC(false);
    private DutyCycleOut feederMotorRequest = new DutyCycleOut(0).withEnableFOC(false);
    private DutyCycleOut shooterMotorLeftRequest = new DutyCycleOut(0).withEnableFOC(false);
    private DutyCycleOut intakeMotorRequest = new DutyCycleOut(0).withEnableFOC(false);
    public void updateInputs(FuelIOInputs inputs) {

    }

    /**
     * Set up the Left Intake Launcher motor,
     * The configuration MUST set up and have the following configurations ; stator
     * current limit, neutral mode , inverted
     */
    public void configureShooterRight() {
        shooterMotorRight = new TalonFX(SHOOTER_RIGHT_MOTOR_ID, new CANBus("rio"));
        var shooterConfigure = new TalonFXConfiguration();
        shooterConfigure.MotorOutput.NeutralMode = NeutralModeValue.Coast;
        shooterConfigure.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
        shooterConfigure.CurrentLimits.StatorCurrentLimit = SHOOTER_MOTOR_CURRENT_LIMIT;
        shooterConfigure.CurrentLimits.StatorCurrentLimitEnable = true;
        // shooterConfigure.CurrentLimits.SupplyCurrentLimit = SHOOTER_MOTOR_SUPPLY_LIMIT;
        // shooterConfigure.CurrentLimits.SupplyCurrentLimitEnable = true;
        shooterMotorRight.getConfigurator().apply(shooterConfigure);
        shooterMotorRight.setControl(new StrictFollower(SHOOTER_LEFT_MOTOR_ID));
    }
    public void configureShooterLeft() {
        shooterMotorLeft = new TalonFX(SHOOTER_LEFT_MOTOR_ID, new CANBus("rio"));
        var shooterConfigure = new TalonFXConfiguration();
        shooterConfigure.MotorOutput.NeutralMode = NeutralModeValue.Coast;
        shooterConfigure.MotorOutput.Inverted = InvertedValue.CounterClockwise_Positive;
        shooterConfigure.CurrentLimits.StatorCurrentLimit = SHOOTER_MOTOR_CURRENT_LIMIT;
        shooterConfigure.CurrentLimits.StatorCurrentLimitEnable = true;
        // shooterConfigure.CurrentLimits.SupplyCurrentLimit = SHOOTER_MOTOR_SUPPLY_LIMIT;
        // shooterConfigure.CurrentLimits.SupplyCurrentLimitEnable = true;
        shooterMotorLeft.getConfigurator().apply(shooterConfigure);
    }

    /**
     * Set up the Right Intake Launcher motor,
     * The configuration MUST set up and have the following configurations ; stator
     * current limit, neutral mode , inverted
     */
    public void configureIntake() {
        intakeMotor = new TalonFX(INTAKE_MOTOR_ID, new CANBus("rio"));
        var intakeConfigure = new TalonFXConfiguration();
        intakeConfigure.MotorOutput.NeutralMode = NeutralModeValue.Coast;
        intakeConfigure.MotorOutput.Inverted = InvertedValue.CounterClockwise_Positive;
        intakeConfigure.CurrentLimits.StatorCurrentLimit = INTAKE_MOTOR_CURRENT_LIMIT;
        intakeConfigure.CurrentLimits.StatorCurrentLimitEnable = true;
        intakeConfigure.CurrentLimits.SupplyCurrentLimit = INTAKE_MOTOR_SUPPLY_LIMIT;
        intakeConfigure.CurrentLimits.SupplyCurrentLimitEnable = true;
        intakeMotor.getConfigurator().apply(intakeConfigure);
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
    public void setIntakePower(double power) {
        intakeMotor.setControl(intakeMotorRequest.withOutput(power)); // positive for shooting
    }

    public void setShooterRightPower(double power) {
        shooterMotorRight.setControl(shooterMotorRightRequest.withOutput(power));
    }

    public void setShooterLeftPower(double power) {
        shooterMotorLeft.setControl(shooterMotorLeftRequest.withOutput(power));
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

    public StatusSignal<AngularVelocity> getLeftShooterMotorVelocity() {
        return shooterMotorLeft.getVelocity();
    }

    public StatusSignal<AngularVelocity> getRightShooterMotorVelocity() {
        return shooterMotorRight.getVelocity();
    }

    public void stop() {        
        setShooterRightPower(ShooterConstants.SHOOTER_STOP_PERCENT);
        setIntakePower(ShooterConstants.INTAKE_STOP_PERCENT);
        setFeederRoller(ShooterConstants.FEEDER_STOP_PERCENT);
        // feedMotor.stopMotor();
        // intakeMotor.stopMotor();
    }
}
