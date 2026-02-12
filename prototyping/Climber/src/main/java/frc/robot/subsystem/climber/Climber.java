package frc.robot.subsystem.climber;

import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.SparkMax;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.configs.TalonFXConfigurator;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Climber extends SubsystemBase {
  private final TalonFX climberMotor;

  /** Creates a new CANBallSubsystem. */
  public Climber() {

    // create brushed motors for each of the motors on the launcher mechanism
    climberMotor = new TalonFX(1);

     var climbconfig = new TalonFXConfiguration();
      climbconfig.MotorOutput.NeutralMode = NeutralModeValue.Coast;
      climbconfig.MotorOutput.Inverted = InvertedValue.CounterClockwise_Positive;
      climbconfig.CurrentLimits.StatorCurrentLimit = 40;
      climbconfig.CurrentLimits.StatorCurrentLimitEnable = true;

    // create the configuration for the climb moter, set a current limit and apply
    // the config to the controller
    climberMotor.getConfigurator().apply(climbconfig);
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