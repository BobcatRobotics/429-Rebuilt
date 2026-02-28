// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystem.Fuel;

import static frc.robot.Constants.FuelConstants.FEEDER_MOTOR_ID;
import static frc.robot.Constants.FuelConstants.SHOOTER_INTAKE_MOTOR_ID;
import static frc.robot.Constants.FuelConstants.SHOOTER_MOTOR_ID;
import static frc.robot.Constants.FuelConstants.FEEDER_MOTOR_CURRENT_LIMIT;
import static frc.robot.Constants.FuelConstants.SHOOTER_MOTOR_CURRENT_LIMIT;


import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.StrictFollower;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Fuel extends SubsystemBase {

  private DutyCycleOut shooterMotorRequest = new DutyCycleOut(0);
  private DutyCycleOut feederMotorRequest = new DutyCycleOut(0);
  private final TalonFX shooterMotor;
  private final TalonFX shooterIntakeMotor;
  private final TalonFX feedMotor;

  /** Creates a new CANBallSubsystem. */
  public Fuel() {
    // create brushed motors for each of the motors on the launcher mechanism
    shooterIntakeMotor = new TalonFX(SHOOTER_INTAKE_MOTOR_ID, new CANBus("rio"));
    feedMotor = new TalonFX(FEEDER_MOTOR_ID, new CANBus("rio"));
    shooterMotor = new TalonFX(SHOOTER_MOTOR_ID, new CANBus("rio"));

    // create the configuration for the feeder roller, set a current limit and apply
    // the config to the controller

    var feederConfigure = new TalonFXConfiguration();
      feederConfigure.MotorOutput.NeutralMode = NeutralModeValue.Coast;
      feederConfigure.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
      feederConfigure.CurrentLimits.StatorCurrentLimit = FEEDER_MOTOR_CURRENT_LIMIT;
      feederConfigure.CurrentLimits.StatorCurrentLimitEnable = true;

    var shooterConfigure = new TalonFXConfiguration();
      shooterConfigure.MotorOutput.NeutralMode = NeutralModeValue.Coast;
      shooterConfigure.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
      shooterConfigure.CurrentLimits.StatorCurrentLimit = SHOOTER_MOTOR_CURRENT_LIMIT;
      shooterConfigure.CurrentLimits.StatorCurrentLimitEnable = true;

    var shooterIntakeConfigure = new TalonFXConfiguration();
      shooterIntakeConfigure.MotorOutput.NeutralMode = NeutralModeValue.Coast;
      shooterIntakeConfigure.MotorOutput.Inverted = InvertedValue.CounterClockwise_Positive;
      shooterIntakeConfigure.CurrentLimits.StatorCurrentLimit = SHOOTER_MOTOR_CURRENT_LIMIT;
      shooterIntakeConfigure.CurrentLimits.StatorCurrentLimitEnable = true;

    // create the configuration for the launcher roller, set a current limit, set
    // the motor to inverted so that positive values are used for both intaking and
    // launching, and apply the config to the controller
    shooterIntakeMotor.getConfigurator().apply(shooterIntakeConfigure);
    feedMotor.getConfigurator().apply(feederConfigure);
    shooterMotor.getConfigurator().apply(shooterConfigure);


    
    shooterMotor.setControl(new StrictFollower(SHOOTER_INTAKE_MOTOR_ID));

    // put default values for various fuel operations onto the dashboard
    // all commands using this subsystem pull values from the dashbaord to allow
    // you to tune the values easily, and then replace the values in Constants.java
    // with your new values. For more information, see the Software Guide.
    SmartDashboard.putNumber("Intaking feeder roller value", 0.8);
    SmartDashboard.putNumber("Intaking intake roller value", 0.6);
    SmartDashboard.putNumber("Launching feeder roller value", 0.6);
    SmartDashboard.putNumber("Launching launcher roller value", 0.85);
    //SmartDashboard.putNumber("Spin-up feeder roller value", SPIN_UP_FEEDER_VOLTAGE);
  }

  // A method to set the voltage of the intake roller
  public void setIntakeLauncherRoller(double power) {
    shooterIntakeMotor.setControl(shooterMotorRequest.withOutput(power)); // positive for shooting
    //shooterMotor.set(power);
  }

  // A method to set the voltage of the intake roller
  public void setFeederRoller(double power) {
    feedMotor.setControl(feederMotorRequest.withOutput(power)); // positive for shooting
  }

  // A method to stop the rollers
  public void stop() {
    feedMotor.stopMotor();
    shooterIntakeMotor.stopMotor();
    //shooterMotor.set(0);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}