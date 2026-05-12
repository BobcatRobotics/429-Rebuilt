package frc.robot;

import org.bobcatrobotics.GameSpecific.Rebuilt.HubUtil;
import org.bobcatrobotics.Util.Interpolators.SingleOutputInterpolator;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.robot.Constants.ShooterConstants;
import frc.robot.commands.DriveCommands;
import frc.robot.subsystems.drive.Drive;

public class RobotState {
    private static RobotState instance;
    private Alliance alliance = Alliance.Red;
    public Translation2d hubLocation = HubUtil.getMyHubCoordinates(alliance).toPose2d().getTranslation();
    public Translation2d passLocation = new Translation2d();
    private double distanceToHub = 0.0;
    private double distanceToPass = 0.0;
    private double passingVelocity = ShooterConstants.PASSING_SPEEDS[0];
    private double shooterVelocity = ShooterConstants.SHOOTER_SPEEDS[0];

    private RobotState(Drive drive){
      passLocation = HubUtil.getMyPassingCoordinates(alliance, drive).toPose2d().getTranslation();
    }

    public SingleOutputInterpolator shootingInterpolator = new SingleOutputInterpolator(ShooterConstants.SHOOTER_DISTANCES, ShooterConstants.SHOOTER_SPEEDS, false);
    public SingleOutputInterpolator passingInterpolator = new SingleOutputInterpolator(ShooterConstants.PASSING_DISTANCES, ShooterConstants.PASSING_SPEEDS, true);



    public static RobotState getInstance() {
      if (instance == null)
      {
        instance = new RobotState();
      }
      return instance;
    }

    private RobotState(){

    };

    public void setAlliance(Alliance alliance) {
      this.alliance = alliance;
    }

    public double getDistanceToPass() {
      return distanceToPass;
    }

    public void setDistanceToPass(double passDistance) { 
      distanceToPass = passDistance; 
      passingVelocity = passingInterpolator.getAsList(distanceToPass).get(0);
    }


    public double getDistanceToHub() { 
      return distanceToHub; 
    }

    public void setDistanceToHub(double distance) { 
      distanceToHub = distance; 
      shooterVelocity = shootingInterpolator.getAsList(distanceToHub).get(0);
    }

    public double getShooterVelocity() { 
      return shooterVelocity; 
    }

        public double getPassingVelocity() { 
      return passingVelocity; 
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
            new Pose2d(2.5, 4.135, new Rotation2d()),
            new Pose2d(1.45, 4.135, new Rotation2d())
          };
        }
        if(alliance == Alliance.Blue && isLeftSideTower == false){
          return new Pose2d[]{
            new Pose2d(2.5, 3.225, new Rotation2d()),
            new Pose2d(1.45, 3.225, new Rotation2d())
          };
        }
        return new Pose2d[] {new Pose2d(), new Pose2d()};
    }
    public void setPassingLocation(Translation2d loc){
      passLocation = loc;
    }
    public Translation2d getPassingCoordinate(){
      return passLocation;
    }
}