package frc.robot.subsystems.fuel;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Fuel extends SubsystemBase {
    private final FuelIO io;
    private final FuelIOInputsAutoLogged inputs = new FuelIOInputsAutoLogged();

    public Fuel(FuelIO io) {
        this.io = io;
        this.io.configureFeeder();
        this.io.configureShooterLeft();
        this.io.configureShooterRight();
        this.io.configureIntake();
        // create brushed motors for each of the motors on the launcher mechanism

    // create the configuration for the launcher roller, set a current limit, set
    // the motor to inverted so that positive values are used for both intaking and
    // launching, and apply the config to the controller

    // put default values for various fuel operations onto the dashboard
    // all commands using this subsystem pull values from the dashbaord to allow
    // you to tune the values easily, and then replace the values in Constants.java
    // with your new values. For more information, see the Software Guide.
    SmartDashboard.putNumber("Intaking feeder roller value", 0.8);
    SmartDashboard.putNumber("Intaking intake roller value", 0.6);
    SmartDashboard.putNumber("Launching feeder roller value", 0.6);
    SmartDashboard.putNumber("Launching launcher roller value", 0.85);
    }

    /**
     * Sets the velocity of the intake Launcher.
     * This is PID based and velocityvoltage control mode.
     * This is more advanced and must be completed after manual control has been
     * succesfully implemented.
     * 
     * @param velSetpoint
     */
    public void setShooterRightPower(double velSetpoint) {
        io.setShooterRightPower(velSetpoint);
    }

    public void setShooterLeftPower(double velSetpoint) {
        io.setShooterLeftPower(velSetpoint);
    }

    public void setIntakePower(double velSetpoint) {
        io.setIntakePower(velSetpoint);
    }

    /**
     * Sets the speed of the intake Launcher.
     * This is not PID based and a duty cycle output control mode.
     * Start with this function before doing the setIntakeLauncherRollerVelocity.
     * 
     * @param power
     */
    public void setIntakeLauncherRoller(double power) {
        io.setIntakeLauncherRoller(power);
    }

    /**
     * Sets the velocity of the intake Launcher.
     * This is PID based and velocityvoltage control mode.
     * This is more advanced and must be completed after manual control has been
     * succesfully implemented.
     * 
     * @param velSetpoint
     */
    public void setFeederRollerVelocity(double velSetpoint) {
        io.setFeederRollerVelocity(velSetpoint);
    }

    /**
     * Sets the speed of the intake Launcher.
     * This is not PID based and a duty cycle output control mode.
     * Start with this function before doing the setFeederRollerVelocity.
     * 
     * @param power
     */
    public void setFeederRoller(double power) {
        io.setFeederRoller(power);
    }

    public void stop() {
        io.stop();
    }
    @Override
    public void periodic() {
        io.updateInputs(inputs);
        Logger.processInputs("Fuel", inputs);
    }
}
