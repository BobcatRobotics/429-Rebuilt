// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import org.bobcatrobotics.GameSpecific.Rebuilt.HubUtil;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
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

      public static final class ShooterConstants {
    // Motor controller IDs for Fuel Mechanism motors
    public static final int FEEDER_MOTOR_ID = 18;
    public static final int SHOOTER_LEFT_MOTOR_ID = 16;
    public static final int SHOOTER_RIGHT_MOTOR_ID = 17;

    // Current limit for fuel mechanism motors.
    public static final int FEEDER_MOTOR_CURRENT_LIMIT = 70;
    public static final int SHOOTER_MOTOR_CURRENT_LIMIT = 70;
    //public static final int SHOOTER_MOTOR_SUPPLY_LIMIT = 70;

    // All values likely need to be tuned based on your robot
    public static final double FEEDER_INTAKING_PERCENT = -0.40;
    public static final double FEEDER_EJECT_PERCENT = 0.5;
    public static final double FEEDER_LAUNCHING_PERCENT = 0.2;
    public static final double FEEDER_SPIN_UP_PRE_LAUNCH_PERCENT = -0.2;

    public static final double SHOOTER_VELOCITY = 104; 
    public static final double SHOOTER_EJECT_VELOCITY = -85.5;

    public static final double SHOOTER_STOP_PERCENT = 0;
    public static final double INTAKE_STOP_PERCENT = 0;
    public static final double FEEDER_STOP_PERCENT = 0;

    public static final double SHOOTER_PERCENT_MID = 0.83;
    public static final double SHOOTER_EJECT_PERCENT_MID = 0.80;

    public static final double SHOOTER_PERCENT_CLOSE = 0.75;
    public static final double SHOOTER_EJECT_PERCENT_CLOSE = 0.70;

    public static final double SHOOTING_DISTANCE_OFFSET = 36.5; //in inches 23.5 for half of hub, 13 for half of robot

    public static final double SPIN_UP_SECONDS = 1;

    public static final double[] SHOOTER_DISTANCES = {40, 50, 60, 70, 80, 90, 100, 110, 120, 130};

    public static final double[] SHOOTER_SPEEDS = {70, 72.5, 75, 81, 84, 88, 91.5, 94, 97, 104};
  }

    public static final class ClimbConstatns {
    // Motor controller IDs for Climb motor
    public static final int CLIMBER_MOTOR_ID = 20;

    // Current limit for climb motor
    public static final int CLIMBER_MOTOR_CURRENT_LIMIT = 30;
    public static final int CLIMBER_MOTOR_STATOR_LIMIT = 20;
    public static final int CLIMBER_MOTOR_NEGATIVE_ROTATIONS = -1;
    public static final int CLIMBER_MOTOR_POSITIVE_ROTATIONS = 1;
    // Percentage to power the motor both up and down
    public static final double CLIMBER_MOTOR_DOWN_PERCENT = -0.7;
    public static final double CLIMBER_MOTOR_UP_PERCENT = 1;
    public static final double CLIMBER_AUTO_DOWN_PERCENT = -1;

    public static final double CLIMBER_PRECLIMB = -268;
    public static final double CLIMBER_CLIMBED = 180;
    
    public static final double CLIMBER_STOP = 0;
  }

  public static final class IntakeConstants {
    // Motor controller IDs for Fuel Mechanism motors
     public static final int FEEDER_MOTOR_ID = 18;
     public static final int INTAKE_MOTOR_ID = 19;

    // Current limit for fuel mechanism motors.
    public static final int FEEDER_MOTOR_CURRENT_LIMIT = 40;
    public static final int INTAKE_MOTOR_CURRENT_LIMIT = 40;
    public static final int INTAKE_MOTOR_SUPPLY_LIMIT = 40;

    // All values likely need to be tuned based on your robot
    public static final double FEEDER_INTAKING_PERCENT = -0.4;
    public static final double FEEDER_EJECT_PERCENT = 0.4;
    public static final double FEEDER_STOP_PERCENT = 0;
    public static final double FEEDER_LAUNCHING_PERCENT = 0.3;
    public static final double FEEDER_SPIN_UP_PRE_LAUNCH_PERCENT = -0.3;

    public static final double INTAKE_PERCENT = 0.6;
    public static final double INTAKE_EJECT_PERCENT = -0.6;
    public static final double INTAKE_STOP_PERCENT = 0;

    public static final double SPIN_UP_SECONDS = 0.75;
  }

}
