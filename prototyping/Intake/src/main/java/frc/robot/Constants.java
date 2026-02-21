// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj.RobotBase;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>
 * It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
    public static final Mode simMode = Mode.SIM;
    public static final Mode currentMode = RobotBase.isReal() ? Mode.REAL : simMode;

    public static enum Mode {
        /** Running on a real robot. */
        REAL,

        /** Running a physics simulator. */
        SIM,

        /** Replaying from a log file. */
        REPLAY
    }
        public static final class IntakeConstants {
        public static InvertedValue intakeMotorInvert = InvertedValue.Clockwise_Positive;
        public static NeutralModeValue intakeMotorBrakeMode = NeutralModeValue.Brake;
        public static double kTopP = 0.00;
        public static double kTopV = 0.00;
        public static double kTopS = 0.00;
        public static double topCurrentLimit = 10;

        public static double idleRollerSpeed = 0;
        public static double forwardRollerSpeed = 1.0;
        public static double reverseRollerSpeed = -1.0;
        
        // Motor controller IDs for Feed motor
        public static final int FEEDER_MOTOR_ID = 18;
        // Motor controller IDs for Intake motor
        public static final int SHOOTER_INTAKE_MOTOR_ID = 16;
        public static final int SHOOTER_MOTOR_ID = 17;
    }
     public static final class FuelConstants {
    // Motor controller IDs for Fuel Mechanism motors

    // Current limit for fuel mechanism motors.
    public static final int FEEDER_MOTOR_CURRENT_LIMIT = 40;
    public static final int LAUNCHER_MOTOR_CURRENT_LIMIT = 40;

    // All values likely need to be tuned based on your robot
    public static final double FEEDER_INTAKING_PERCENT = -.1; 
    public static final double FEEDER_LAUNCHING_PERCENT = 0.1;
    public static final double FEEDER_SPIN_UP_PRE_LAUNCH_PERCENT = -0.1;

    public static final double INTAKE_INTAKING_PERCENT = 0.1;
    public static final double SHOOTER_INTAKE_PERCENT = .085;
    public static final double INTAKE_EJECT_PERCENT = -0.1;

    public static final double SPIN_UP_SECONDS = 0.75;
  }
}
