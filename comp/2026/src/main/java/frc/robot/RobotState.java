package frc.robot;

import org.bobcatrobotics.GameSpecific.Rebuilt.HubUtil;
import org.bobcatrobotics.Util.Interpolators.SingleOutputInterpolator;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.robot.Constants.ShooterConstants;

public class RobotState {
    private static RobotState instance;
    public Alliance alliance = Alliance.Red;
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

}