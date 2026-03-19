package frc.robot.subsystems.climber;

import org.littletonrobotics.junction.AutoLog;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

public interface ClimberIO {
    @AutoLog
    public class ClimberIOInputs {
        public double positionOfClimberMotor = 0.0;
        public double currentOfclimberMotor = 0.0;
    }

    public default void updateInputs(ClimberIOInputs inputs) {

    }

    /**
     * Set up the Left Intake Launcher motor,
     * The configuration MUST set up and have the following configurations ; stator
     * current limit, neutral mode , inverted state
     */
    public default void configureClimber() {

    }

    public default Command disableLimits(){
        return Commands.none();
    }

    public default Command enableLimits(){
        return Commands.none();
    }



    /**
     * Sets the position of the climber.
     * This is PID based and position control mode.
     * 
     * @param angleSetPoint
     */
    public default void setClimberPosition(double angleSetPoint){

    }
    /**
     * Sets the output of the climber.
     * This is not PID based and will apply output to the motor.
     * 
     */
    public default void setClimberPower(double power){

    }
    public default void stop(){

    }
}
