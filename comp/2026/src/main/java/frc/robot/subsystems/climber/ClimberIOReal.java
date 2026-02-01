package frc.robot.subsystems.climber;

public class ClimberIOReal implements ClimberIO {
    
    public void updateInputs(ClimberIOInputs inputs) {

    }

    /**
     * Set up the Left Intake Launcher motor,
     * The configuration MUST set up and have the following configurations ; stator
     * current limit, neutral mode , inverted state
     */
    public void configureClimber() {

    }

    /**
     * Sets the position of the climber.
     * This is PID based and position control mode.
     * 
     * @param angleSetPoint
     */
    public void setClimberPosition(double angleSetPoint) {

    }

    /**
     * Sets the output of the climber.
     * This is not PID based and will apply output to the motor.
     * 
     */
    public void setClimber(double power) {

    }

    public void stop() {

    }
}
