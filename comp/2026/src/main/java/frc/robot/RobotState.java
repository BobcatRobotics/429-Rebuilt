package frc.robot;

import org.bobcatrobotics.GameSpecific.Rebuilt.HubUtil;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class RobotState {
    private static RobotState instance;
    public Alliance alliance = Alliance.Red;
    public Translation2d hubLocation = HubUtil.getMyHubCoordinates(alliance).toPose2d().getTranslation();

    public static RobotState getInstance() {
    if (instance == null)
    {
      instance = new RobotState();
    }
    return instance;
  }
}