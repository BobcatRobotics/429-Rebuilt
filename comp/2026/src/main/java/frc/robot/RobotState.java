package frc.robot;

import org.bobcatrobotics.GameSpecific.Rebuilt.HubUtil;
import org.bobcatrobotics.Util.Interpolators.SingleOutputInterpolator;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.robot.Constants.ShooterConstants;
import frc.robot.subsystems.fuel.Fuel;

public class RobotState {
    private static RobotState instance;
    private Alliance alliance = Alliance.Red;
    public Translation2d hubLocation = HubUtil.getMyHubCoordinates(alliance).toPose2d().getTranslation();
    private double distanceToHub = 0.0;
    private double shooterVelocity = ShooterConstants.SHOOTER_SPEEDS[0];
    private boolean isAligned = false;
    private boolean isAutoDriving = false;
    private boolean isAutoAligning = false;
    private boolean isBlockerDeployed = false;
    private boolean isAllianceShiftActive = false;
    public boolean isTargetShooterVelocityReached = false;
    private double actualShooterVelocity = 0.0;

    public SingleOutputInterpolator interpolator = new SingleOutputInterpolator(ShooterConstants.SHOOTER_DISTANCES, ShooterConstants.SHOOTER_SPEEDS, false);

    public static RobotState getInstance() {
      if (instance == null)
      {
        instance = new RobotState();
      }
      return instance;
    }

    private RobotState(Fuel fuel){
      actualShooterVelocity = fuel.getRightShooterMotorVelocity().getValueAsDouble();
    }

    public Alliance getAlliance() {
      return alliance;
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

    public boolean targetShooterVelocityReached(double shooterVelocity, double actualShooterVelocity){
      if(actualShooterVelocity > (shooterVelocity - 2) && actualShooterVelocity < (shooterVelocity + 2)){
        return isTargetShooterVelocityReached = true;
      }
      return isTargetShooterVelocityReached = false;
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

    public boolean getIsAutoDriving() {
      return isAutoDriving;
    }
    
    public void setIsAutoDriving(boolean value) {
      isAutoDriving = value;
    }
    
    public boolean getIsAutoAligning() {
      return isAutoAligning;
    }
    
    public void setIsAutoAligning(boolean value) {
      isAutoAligning = value;
    }
    
    public boolean getIsAligned() {
      return isAligned;
    }
    
    public void setIsAligned(boolean value) {
      isAligned = value;
    }
    
    public boolean getIsBlockerDeployed() {
      return isBlockerDeployed;
    }
    
    public void setIsBlockerDeployed(boolean value) {
      isBlockerDeployed = value;
    }
    
    public boolean getIsAllianceShiftActive() {
      return isAllianceShiftActive;
    }
    
    public void setIsAllianceShiftActive(boolean value) {
      isAllianceShiftActive = value;
    }
}