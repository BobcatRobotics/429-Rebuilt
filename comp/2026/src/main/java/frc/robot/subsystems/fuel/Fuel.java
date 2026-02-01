package frc.robot.subsystems.fuel;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Fuel extends SubsystemBase {
    private final FuelIO io;
    private final FuelIOInputsAutoLogged inputs = new FuelIOInputsAutoLogged();

    public Fuel(FuelIO io) {
        this.io = io;
    }

    /**
     * Sets the velocity of the intake Launcher.
     * This is PID based and velocityvoltage control mode.
     * This is more advanced and must be completed after manual control has been
     * succesfully implemented.
     * 
     * @param velSetpoint
     */
    public void setIntakeLauncherRollerVelocity(double velSetpoint) {
        io.setIntakeLauncherRollerVelocity(velSetpoint);
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
