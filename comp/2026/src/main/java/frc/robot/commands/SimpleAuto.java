package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.drive.Drive;

import com.pathplanner.lib.auto.NamedCommands;

public class SimpleAuto extends SequentialCommandGroup{
  public SimpleAuto(Drive drive) {
    addCommands(

        NamedCommands.getCommand("Set Pose"),

        //negative 0.5 for blue, positive 0.5 for red

      DriveCommands.joystickDrive(drive, () -> -0.5, () -> 0, () -> 0.0)
                   .withTimeout(2.0),

      DriveCommands.joystickDrive(drive, () -> 0.0, () -> 0.0, () -> 0.0)
                   .withTimeout(0.02),

      NamedCommands.getCommand("Climb down")
            .withTimeout(2),
 
      NamedCommands.getCommand("Shooter at tower distance")
                    .withTimeout(5),

      NamedCommands.getCommand("Shooter spin")
 
    );
  }
}
 
