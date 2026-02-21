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

    public static final class HopperConstants{

        public static final InvertedValue hopperMotorInvert = InvertedValue.Clockwise_Positive;
        public static final NeutralModeValue hopperMotorBrakeMode = NeutralModeValue.Brake;
        public static final double kTopP = 0;
        public static final double kTopV = 0;
        public static final double kTopS = 0;
        public static final double topCurrentLimit = 0;

        public static final double idleHopperSpeed = 0.0;
        public static final double intakeHopperSpeed = 1.0;
        public static final double outtakeHopperSpeed = -1.0;
    }

    public static final class ClimbConstatns {
    // Motor controller IDs for Climb motor
    public static final int CLIMBER_MOTOR_ID = 7;

    // Current limit for climb motor
    public static final int CLIMBER_MOTOR_CURRENT_LIMIT = 40;
    public static final int CLIMBER_MOTOR_STATOR_LIMIT = 60;
    public static final int CLIMBER_MOTOR_NEGATIVE_ROTATIONS = -1;
    public static final int CLIMBER_MOTOR_POSITIVE_ROTATIONS = 1;
    // Percentage to power the motor both up and down
    public static final double CLIMBER_MOTOR_DOWN_PERCENT = -0.4;
    public static final double CLIMBER_MOTOR_UP_PERCENT = 0.4;
  }

}
