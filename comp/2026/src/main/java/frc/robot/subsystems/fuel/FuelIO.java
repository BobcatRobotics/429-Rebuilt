package frc.robot.subsystems.fuel;

import org.littletonrobotics.junction.AutoLog;

public interface FuelIO {
    @AutoLog
    class FuelIOInputs {
        public double velocityOfLeftIntakeLauncher = 0.0;
        public double velocityOfRightIntakeLauncher = 0.0;
        public double currentOfLeftIntakeLauncher = 0.0;
        public double currentOfRightIntakeLauncher = 0.0;
        public double velocityOfIndexer = 0.0;
        public double currentOfIndexer = 0.0;
    }

    public default void updateInputs(FuelIOInputs inputs) {

    }

    /**
     * Set up the Left Intake Launcher motor,
     * The configuration MUST set up and have the following configurations ; stator
     * current limit, neutral mode , inverted
     */
    public default void configureShooter() {

    }

    /**
     * Set up the Right Intake Launcher motor,
     * The configuration MUST set up and have the following configurations ; stator
     * current limit, neutral mode , inverted
     */
    public default void configureShooter2() {

    }

    /**
     * Set up the Indexer motor,
     * The configuration MUST set up and have the following configurations ; stator
     * current limit
     */
    public default void configureFeeder() {

    }

    public default void configureIntake() {

    }

    /**
     * Sets the velocity of the intake Launcher.
     * This is PID based and velocityvoltage control mode.
     * 
     * 
     */
    public default void setShooterPower(double power) {

    }

    public default void setShooter2Power(double power){

    }

    public default void setIntakePower(double power) {
        
    }

    /**
     * Sets the speed of the intake Launcher.
     * This is not PID based and a duty cycle output control mode.
     * 
     * @param power
     */
    public default void setIntakeLauncherRoller(double power) {

    }

    /**
     * Sets the velocity of the intake Launcher.
     * This is PID based and velocityvoltage control mode.
     * 
     * @param velSetpoint
     */
    public default void setFeederRollerVelocity(double velSetpoint) {

    }

    /**
     * Sets the speed of the intake Launcher.
     * This is not PID based and a duty cycle output control mode.
     * 
     * @param power
     */
    public default void setFeederRoller(double power) {

    }

    public default void stop() {

    }
}
