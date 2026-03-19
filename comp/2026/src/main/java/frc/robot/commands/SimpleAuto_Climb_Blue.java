package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.drive.Drive;

import com.pathplanner.lib.auto.NamedCommands;

public class SimpleAuto_Climb_Blue extends SequentialCommandGroup{
  public SimpleAuto_Climb_Blue(Drive drive) {
    addCommands(

    NamedCommands.getCommand("Set Pose"),

      new ParallelCommandGroup(DriveCommands.joystickDrive(drive, () -> 0, () -> 0, () -> 0.0).withTimeout(2.25)
              .andThen(DriveCommands.joystickDrive(drive, () -> 0.5, () -> 0, () -> 0.0)),
              NamedCommands.getCommand("Climb down"))
                   .withTimeout(5)
                   .finallyDo(() -> NamedCommands.getCommand("Stop Climber")),
 
      

      DriveCommands.joystickDrive(drive, () -> 0.0, () -> 0.0, () -> 0.0)
                   .withTimeout(0.02),
 
      NamedCommands.getCommand("Shooter at tower distance")
                    .withTimeout(5)
                    .finallyDo(() -> NamedCommands.getCommand("Stop Shooting")),

      
      // NamedCommands.getCommand("Shooter spin")
      //     .withTimeout(2),

      NamedCommands.getCommand("Climb Up")
                    .withTimeout(6)
                     .finallyDo(() -> NamedCommands.getCommand("Stop Climber"))
    );
  }
}