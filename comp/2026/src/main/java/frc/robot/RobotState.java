package frc.robot;

import org.bobcatrobotics.GameSpecific.Rebuilt.HubUtil;
import org.bobcatrobotics.Util.Interpolators.SingleOutputInterpolator;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.robot.Constants.ShooterConstants;

public class RobotState {
    private static RobotState instance;
    private Alliance alliance = Alliance.Red;
    public Translation2d hubLocation = HubUtil.getMyHubCoordinates(alliance).toPose2d().getTranslation();
    private double distanceToHub = 0.0;
    private double shooterVelocity = ShooterConstants.SHOOTER_SPEEDS[0];

    public SingleOutputInterpolator interpolator = new SingleOutputInterpolator(ShooterConstants.SHOOTER_DISTANCES, ShooterConstants.SHOOTER_SPEEDS, false);

    public static RobotState getInstance() {
      if (instance == null)
      {
        instance = new RobotState();
      }
      return instance;
    }

    public void setAlliance(Alliance alliance) {
      this.alliance = alliance;
    }

    public double getDistanceToHub() { 
      return distanceToHub; 
    }

    public void setDistanceToHub(double distance) { 
      distanceToHub = distance; 
      shooterVelocity = interpolator.getAsList(distanceToHub).get(0);
    }

    public double getShooterVelocity() { 
      return shooterVelocity; 
    }

    public Pose2d[] getTowerLocation(boolean isLeftSideTower){
        if(alliance == Alliance.Red && isLeftSideTower == true){
            return new Pose2d[]{
              new Pose2d(14.5, 4.034536, new Rotation2d(Math.toRadians(180))), 
              new Pose2d(15.144, 4.034536, new Rotation2d(Math.toRadians(180)))
            };
        }
        if(alliance == Alliance.Red && isLeftSideTower == false){
            return new Pose2d[] {
            new Pose2d(14.5, 4.593, new Rotation2d(Math.toRadians(180))),
            new Pose2d(15.144, 4.593, new Rotation2d(Math.toRadians(180)))
          };
        }
        if(alliance == Alliance.Blue && isLeftSideTower == true){
          return new Pose2d[]{
            new Pose2d(2.8, 4.1, new Rotation2d()),
            new Pose2d(1.6, 4.1, new Rotation2d())
          };
        }
        if(alliance == Alliance.Blue && isLeftSideTower == false){
          return new Pose2d[]{
            new Pose2d(2.8, 3.275, new Rotation2d()),
            new Pose2d(1.6, 3.275, new Rotation2d())
          };
        }
        return new Pose2d[] {new Pose2d(), new Pose2d()};
    }
}