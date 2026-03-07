package frc.robot.subsystems.climber;

import static frc.robot.Constants.ClimbConstatns.CLIMBER_MOTOR_CURRENT_LIMIT;
import static frc.robot.Constants.ClimbConstatns.CLIMBER_MOTOR_ID;
import static frc.robot.Constants.ClimbConstatns.CLIMBER_MOTOR_NEGATIVE_ROTATIONS;
import static frc.robot.Constants.ClimbConstatns.CLIMBER_MOTOR_POSITIVE_ROTATIONS;
import static frc.robot.Constants.ClimbConstatns.CLIMBER_MOTOR_STATOR_LIMIT;

import com.thethriftybot.devices.ThriftyNova;
import com.thethriftybot.devices.ThriftyNova.CurrentType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Climber extends SubsystemBase {
  private final ThriftyNova climberMotor;
 @SuppressWarnings("resource")
  public Climber() {
    climberMotor = new ThriftyNova(CLIMBER_MOTOR_ID)
        .setBrakeMode(true)
        .setInversion(false)
        .setSoftLimits(CLIMBER_MOTOR_NEGATIVE_ROTATIONS, CLIMBER_MOTOR_POSITIVE_ROTATIONS)
        .setMaxCurrent(CurrentType.STATOR, CLIMBER_MOTOR_STATOR_LIMIT)
        .setMaxCurrent(CurrentType.SUPPLY, CLIMBER_MOTOR_CURRENT_LIMIT);
  }


// A method to set the percentage of the climber
  public void setClimber(double power) {
    climberMotor.set(power);
  }

  // A method to stop the climber
  public void stop() {
    climberMotor.set(0);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}