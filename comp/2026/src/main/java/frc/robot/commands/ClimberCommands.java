package frc.robot.commands;

import java.util.Set;

import org.bobcatrobotics.Commands.ActionFactory;

import edu.wpi.first.math.util.Units;
import frc.robot.RobotState;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.Constants.ClimbConstants;
import frc.robot.subsystems.climber.Climber;
import frc.robot.subsystems.drive.Drive;

public class ClimberCommands {
    public static Command climbToLevel(Drive drive, Climber climber, boolean isLeftSideTower, double level) {
        return Commands.defer(() ->
            DriveCommands.driveToPose(RobotState.getInstance().getTowerLocation(isLeftSideTower)[0], ClimbConstants.AUTO_CLIMB_VELOCITY, ClimbConstants.AUTO_CLIMB_ACCEL)
                .andThen(DriveCommands.driveToPose(RobotState.getInstance().getTowerLocation(isLeftSideTower)[1], ClimbConstants.AUTO_CLIMB_VELOCITY, ClimbConstants.AUTO_CLIMB_ACCEL))
                .andThen(new ActionFactory().singleAction("Straight-Command", () -> drive.stopWithStraight(), drive))
                .andThen(Commands.run(() -> climber.setClimberPower(ClimbConstants.CLIMBER_MOTOR_UP_PERCENT), climber)
                    .until(() -> Units.radiansToDegrees(Math.atan2(drive.getGravityVectorZ(), drive.getGravityVectorX())) <= level))
        ,Set.of(drive));
    }
}
