package frc.robot.subsystems.fuel;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;

public class Fuel extends SubsystemBase {
    private final TalonFX FeedMotor;
    private final TalonFX ShooterMotor;
    
    private final Joystick m_controller = new Joystick(0);
    private final JoystickButton velSetpoint = new JoystickButton(m_controller, XboxController.Button.kA.value);

    public Fuel(int feedMotorID, int shooterMotorID) {
        FeedMotor = new TalonFX(feedMotorID);
        ShooterMotor = new TalonFX(shooterMotorID);
    }

    /**
     * @param velSetpoint
    */
    public void setIntakeLauncherRollerVelocity(double velSetpoint) {
        FeedMotor.setControl(new com.ctre.phoenix6.controls.VelocityVoltage(velSetpoint));
    }

    /**
     * @param power
    */
    public void setShooterRollerVelocity(double power) {
        ShooterMotor.setControl(new com.ctre.phoenix6.controls.VoltageOut(power));
    }

    /**
     * @param velSetpoint
    */
    public void setFeederRollerVelocity(double velSetpoint) {
        FeedMotor.setControl(new com.ctre.phoenix6.controls.VelocityVoltage(velSetpoint));
    }

    /**
     * @param power
    */
    public void setFeederRoller(double power) {
        FeedMotor.setControl(new com.ctre.phoenix6.controls.VoltageOut(power));
    }

    public void stop() {
       FeedMotor.stopMotor();
       ShooterMotor.stopMotor();
    }

    @Override
    public void periodic() {
        Logger.recordOutput("Fuel/FeedMotorVelocity", FeedMotor.getVelocity().getValueAsDouble());
        Logger.recordOutput("Fuel/ShooterMotorVelocity", ShooterMotor.getVelocity().getValueAsDouble());
    }
}