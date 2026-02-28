package frc.robot.subsystem.climber;

import static frc.robot.Constants.ClimbConstatns.CLIMBER_MOTOR_CURRENT_LIMIT;
import static frc.robot.Constants.ClimbConstatns.CLIMBER_MOTOR_ID;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Climber extends SubsystemBase {
  private DutyCycleOut climberMotorrequest = new DutyCycleOut(0);
  private final TalonFX climberMotor;

 @SuppressWarnings("resource")
  public Climber() {
    climberMotor = new TalonFX(CLIMBER_MOTOR_ID, new CANBus("rio"));

    var ClimberConfig = new TalonFXConfiguration();
      ClimberConfig.MotorOutput.NeutralMode = NeutralModeValue.Coast;
      ClimberConfig.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
      ClimberConfig.CurrentLimits.StatorCurrentLimit = CLIMBER_MOTOR_CURRENT_LIMIT;
      ClimberConfig.CurrentLimits.StatorCurrentLimitEnable = true;


      climberMotor.getConfigurator().apply(ClimberConfig);
  }

  // A method to set the percentage of the climber
  public void setClimber(double power) {
    climberMotor.setControl(climberMotorrequest.withOutput(power));
  }

  // A method to stop the climber
  public void stop() {
    climberMotor.stopMotor();
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}