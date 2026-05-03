package frc.robot.util;

import java.util.Optional;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class TowerUtil {
    public static Pose2d getTowerLocation(Alliance alliance, boolean isLeftSideTower){
        // if(alliance.isEmpty()){
        //     return new Pose2d(0.0, 0.0, new Rotation2d());
        // }
        if(alliance == Alliance.Red && isLeftSideTower == true){
            return new Pose2d(15.144, 4.593, new Rotation2d(Math.toRadians(180)));
        }
        else if(alliance == Alliance.Red && isLeftSideTower == false){
            return new Pose2d(15.144, 4.034536, new Rotation2d(Math.toRadians(180)));
        }
        else if(alliance == Alliance.Blue && isLeftSideTower == true){
            return new Pose2d(1.4, 4.034536, new Rotation2d());
        }
        else if(alliance == Alliance.Blue && isLeftSideTower == false){
            return new Pose2d(1.4, 3.577, new Rotation2d());
        }
        return new Pose2d();
    }
}