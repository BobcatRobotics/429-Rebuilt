package frc.robot.subsystems.fuel;

public class FuelIOReal implements FuelIO {

    public void updateInputs(FuelIOInputs inputs) {

    }

    /**
     * Set up the Left Intake Launcher motor,
     * The configuration MUST set up and have the following configurations ; stator
     * current limit, neutral mode , inverted
     */
    public void configureLeftIntakeLauncher() {

    }

    /**
     * Set up the Right Intake Launcher motor,
     * The configuration MUST set up and have the following configurations ; stator
     * current limit, neutral mode , inverted
     */
    public void configureRightIntakeLauncher() {

    }

    /**
     * Set up the Indexer motor,
     * The configuration MUST set up and have the following configurations ; stator
     * current limit
     */
    public void configureIndexer() {

    }

    /**
     * Sets the velocity of the intake Launcher.
     * This is PID based and velocityvoltage control mode.
     * This is more advanced and must be completed after manual control has been succesfully implemented.
     * 
     * @param velSetpoint
     */
    public void setIntakeLauncherRollerVelocity(double velSetpoint) {

    }

    /**
     * Sets the speed of the intake Launcher.
     * This is not PID based and a duty cycle output control mode.
     * Start with this function before doing the setIntakeLauncherRollerVelocity.
     * 
     * @param power
     */
    public void setIntakeLauncherRoller(double power) {

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

    }

    public void stop() {

    }
}
