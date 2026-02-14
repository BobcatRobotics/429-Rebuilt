package frc.robot.subsystems.intake;

import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Intake extends SubsystemBase {
    private final TalonFX LeftIntakeLauncher;
    private final TalonFX RightIntakeLauncher;
    private final TalonFX Indexer;

    public Intake() {
        // Use unique CAN IDs for each motor
        LeftIntakeLauncher  = new TalonFX(1);
        RightIntakeLauncher = new TalonFX(2);
        Indexer             = new TalonFX(3);

        // Configure Indexer (feeder) motor
        TalonFXConfiguration feederConfig = new TalonFXConfiguration();
        feederConfig.CurrentLimits.StatorCurrentLimit = Constants.IntakeConstants.LauncherConstants.INDEXER_MOTOR_CURRENT_LIMIT;
        feederConfig.CurrentLimits.StatorCurrentLimitEnable = true;
        Indexer.getConfigurator().apply(feederConfig);

        // Configure right launcher (non-inverted)
        TalonFXConfiguration rightConfig = new TalonFXConfiguration();
        rightConfig.CurrentLimits.StatorCurrentLimit = Constants.IntakeConstants.LauncherConstants.LAUNCHER_MOTOR_CURRENT_LIMIT;
        rightConfig.CurrentLimits.StatorCurrentLimitEnable = true;
        rightConfig.MotorOutput.NeutralMode = NeutralModeValue.Coast;
        RightIntakeLauncher.getConfigurator().apply(rightConfig);

        // Configure left launcher (inverted)
        TalonFXConfiguration leftConfig = new TalonFXConfiguration();
        leftConfig.CurrentLimits.StatorCurrentLimit = Constants.IntakeConstants.LauncherConstants.LAUNCHER_MOTOR_CURRENT_LIMIT;
        leftConfig.CurrentLimits.StatorCurrentLimitEnable = true;
        leftConfig.MotorOutput.NeutralMode = NeutralModeValue.Coast;
        leftConfig.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
        LeftIntakeLauncher.getConfigurator().apply(leftConfig);

        // SmartDashboard defaults — qualify constant names fully
        SmartDashboard.putNumber("Intaking feeder roller value",   Constants.IntakeConstants.LauncherConstants.INDEXER_INTAKING_PERCENT);
        SmartDashboard.putNumber("Intaking intake roller value",   Constants.IntakeConstants.LauncherConstants.INTAKE_INTAKING_PERCENT);
        SmartDashboard.putNumber("Launching feeder roller value",  Constants.IntakeConstants.LauncherConstants.INDEXER_LAUNCHING_PERCENT);
        SmartDashboard.putNumber("Launching launcher roller value",Constants.IntakeConstants.LauncherConstants.LAUNCHING_LAUNCHER_PERCENT);
    }

    public void setIntakeLauncherRoller(double power) {
        LeftIntakeLauncher.set(power);
        RightIntakeLauncher.set(power);
    }

    public void setFeederRoller(double power) {
        Indexer.set(power);
    }

    public void stop() {
        Indexer.set(0);
        LeftIntakeLauncher.set(0);
        RightIntakeLauncher.set(0);
    }

    @Override
    public void periodic() {}
}