
package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.drive.Drive;

import com.pathplanner.lib.auto.NamedCommands;


// Climber needs to go down farther, climbs up to much, need to drive farther.

public class SimpleAuto_Climb_Red extends SequentialCommandGroup{
  public SimpleAuto_Climb_Red(Drive drive) {
    addCommands(

    NamedCommands.getCommand("Set Pose"),

      new ParallelCommandGroup(DriveCommands.joystickDrive(drive, () -> 0, () -> 0, () -> 0.0).withTimeout(2.3)
              .andThen(DriveCommands.joystickDrive(drive, () -> -0.5, () -> 0, () -> 0.0)),
              NamedCommands.getCommand("Pre Climb Auto Set Up"))
                   .withTimeout(4.6)
                   .finallyDo(() -> NamedCommands.getCommand("Stop Climber")),
 
      

      DriveCommands.joystickDrive(drive, () -> 0.0, () -> 0.0, () -> 0.0)
                   .withTimeout(0.02),
 
      NamedCommands.getCommand("Shooter at tower distance")
                    .withTimeout(5),

      NamedCommands.getCommand("Stop Shooting"),

      
      // NamedCommands.getCommand("Shooter spin")
      //     .withTimeout(2),

      NamedCommands.getCommand("Climb Up")
                    .withTimeout(1.65)
                     .finallyDo(() -> NamedCommands.getCommand("Stop Climber"))
    );
  }
}