
// Copyright 2021-2025 FRC 6328
// http://github.com/Mechanical-Advantage
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// version 3 as published by the Free Software Foundation or
// available in the root directory of this project.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.

package frc.robot;

import org.littletonrobotics.junction.Logger;
// import frc.robot.subsystems.roller.RollerSubsystem;
import org.littletonrobotics.junction.networktables.LoggedDashboardChooser;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.commands.PathPlannerAuto;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Power;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
// import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import frc.robot.Constants.ClimbConstatns;
import frc.robot.Constants.FuelConstants;
import frc.robot.Constants.ShooterConstants;
import frc.robot.commands.DriveCommands;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.climber.Climber;
import frc.robot.subsystems.drive.Drive;
import frc.robot.subsystems.drive.GyroIO;
import frc.robot.subsystems.drive.GyroIOPigeon2;
import frc.robot.subsystems.drive.ModuleIO;
import frc.robot.subsystems.drive.ModuleIOSim;
import frc.robot.subsystems.drive.ModuleIOTalonFX;
import frc.robot.subsystems.fuel.Fuel;
import frc.robot.subsystems.shooter.Shooter;
import frc.robot.subsystems.vision.Vision;
import frc.robot.subsystems.vision.VisionIOLimelight;
import org.bobcatrobotics.Commands.ActionFactory;
import org.bobcatrobotics.GameSpecific.Rebuilt.HubData;
import org.bobcatrobotics.GameSpecific.Rebuilt.HubUtil;
import org.bobcatrobotics.Subsystems.AntiTippingLib.AntiTipping;
import static frc.robot.Constants.ClimbConstatns.CLIMBER_MOTOR_CURRENT_LIMIT;
import static frc.robot.Constants.ClimbConstatns.CLIMBER_MOTOR_ID;
import static frc.robot.Constants.ClimbConstatns.CLIMBER_MOTOR_NEGATIVE_ROTATIONS;
import static frc.robot.Constants.ClimbConstatns.CLIMBER_MOTOR_POSITIVE_ROTATIONS;
import static frc.robot.Constants.ClimbConstatns.CLIMBER_MOTOR_STATOR_LIMIT;
import static frc.robot.Constants.FuelConstants.FEEDER_MOTOR_ID;
import static frc.robot.Constants.FuelConstants.SHOOTER_INTAKE_MOTOR_ID;
import static frc.robot.Constants.FuelConstants.SHOOTER_MOTOR_ID;
import static frc.robot.Constants.FuelConstants.FEEDER_MOTOR_CURRENT_LIMIT;
import static frc.robot.Constants.FuelConstants.SHOOTER_MOTOR_CURRENT_LIMIT;
import static frc.robot.Constants.ShooterConstants.FEEDER_MOTOR_ID;
import static frc.robot.Constants.ShooterConstants.SHOOTER_INTAKE_MOTOR_ID;
import static frc.robot.Constants.ShooterConstants.SHOOTER_MOTOR_ID;
import static frc.robot.Constants.ShooterConstants.FEEDER_MOTOR_CURRENT_LIMIT;
import static frc.robot.Constants.ShooterConstants.SHOOTER_MOTOR_CURRENT_LIMIT;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in
 * the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of
 * the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
    // Subsystems    
    private final Shooter shooter;
    private final Fuel fuel;
    private final Climber climber;
    private final Drive drive;
    private final AntiTipping antiTipping;
    private Vision vision;

    // Controller
    private final CommandXboxController controller = new CommandXboxController(0);
    private final CommandXboxController operator = new CommandXboxController(1);

        // Named commands must be registered after the subsystems they use are constructed.

    // Dashboard inputs
    private final LoggedDashboardChooser<Command> autoChooser;

    private final HubUtil hub;

    /**
     * The container for the robot. Contains subsystems, OI devices, and commands.
     */
    public RobotContainer() {
        switch (Constants.currentMode) {
            case REAL:
                // Real robot, instantiate hardware IO implementations
                drive = new Drive(new GyroIOPigeon2(), new ModuleIOTalonFX(TunerConstants.FrontLeft),
                        new ModuleIOTalonFX(TunerConstants.FrontRight),
                        new ModuleIOTalonFX(TunerConstants.BackLeft),
                        new ModuleIOTalonFX(TunerConstants.BackRight));
                fuel = new Fuel();
                climber = new Climber();
                shooter = new Shooter();
                // Vision
                vision = new Vision(drive::addVisionMeasurement, new VisionIOLimelight("", drive::getRotation));
                break;
            case SIM:
                // Sim robot, instantiate physics sim IO implementations
                drive = new Drive(new GyroIO() {
                }, new ModuleIOSim(TunerConstants.FrontLeft),
                        new ModuleIOSim(TunerConstants.FrontRight), new ModuleIOSim(TunerConstants.BackLeft),
                        new ModuleIOSim(TunerConstants.BackRight));
                fuel = new Fuel();
                climber = new Climber();
                shooter = new Shooter();
                break;

            default:
                // Replayed robot, disable IO implementations
                drive = new Drive(new GyroIO() {
                }, new ModuleIO() {
                }, new ModuleIO() {
                }, new ModuleIO() {
                },
                        new ModuleIO() {
                        });
                fuel = new Fuel();
                climber = new Climber();
                shooter = new Shooter();
                break;
        }

        antiTipping = new AntiTipping(() -> drive.getPitch(), () -> drive.getRoll(), 0.04, // kP
                3.0, // tipping threshold (degrees)
                2.5 // max correction speed (m/s)
        );

        NamedCommands.registerCommand("Shooter", new SequentialCommandGroup(
                Commands.run(() -> shooter.setIntakeLauncherRoller(FuelConstants.SHOOTER_INTAKE_PERCENT), shooter), 
                Commands.waitSeconds(2.5),
                Commands.runOnce(() -> shooter.stop(), shooter)));

        NamedCommands.registerCommand("Intake", new SequentialCommandGroup(
                Commands.run(() -> fuel.setFeederRoller(FuelConstants.FEEDER_INTAKING_PERCENT), fuel),
                Commands.waitSeconds(3),
                Commands.runOnce(() -> fuel.stop(), fuel)));

        // Set up auto routines
        autoChooser = new LoggedDashboardChooser<>("Auto Choices", AutoBuilder.buildAutoChooser());

        // Set up SysId routines
        autoChooser.addOption("Shooter", new PathPlannerAuto("Right Trench - Depot - Tower"));
        autoChooser.addOption("Intake", new PathPlannerAuto("Right Trench - Depot - Tower"));
        autoChooser.addOption("Drive Wheel Radius Characterization",
                DriveCommands.wheelRadiusCharacterization(drive));
        autoChooser.addOption("Drive Simple FF Characterization",
                DriveCommands.feedforwardCharacterization(drive));
        autoChooser.addOption("Drive SysId (Quasistatic Forward)",
                drive.sysIdQuasistatic(SysIdRoutine.Direction.kForward));
        autoChooser.addOption("Drive SysId (Quasistatic Reverse)",
                drive.sysIdQuasistatic(SysIdRoutine.Direction.kReverse));
        autoChooser.addOption("Drive SysId (Dynamic Forward)",
                drive.sysIdDynamic(SysIdRoutine.Direction.kForward));
        autoChooser.addOption("Drive SysId (Dynamic Reverse)",
                drive.sysIdDynamic(SysIdRoutine.Direction.kReverse));
       

        // Configure the button bindings
        configureButtonBindings();

        hub = new HubUtil();
    }

    /**
     * Use this method to define your button->command mappings. Buttons can be
     * created by
     * instantiating a {@link GenericHID} or one of its subclasses
     * ({@link edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then
     * passing it to a
     * {@link edu.wpi.first.wpilibj2.command.button.JoystickButton}.
     */
    private void configureButtonBindings() {

        // Default command, normal field-relative drive
        drive.setDefaultCommand(
                DriveCommands.joystickDrive(
                        drive,
                        () -> -controller.getLeftY(),
                        () -> -controller.getLeftX(),
                        () -> -controller.getRightX()));

        fuel.setDefaultCommand(fuel.run(() -> fuel.stop()));
        climber.setDefaultCommand(climber.run(() -> climber.stop()));

        // Lock to 0° when A button is held
        controller
                .a()
                .whileTrue(
                        DriveCommands.joystickDriveAtAngle(
                                drive,
                                () -> -controller.getLeftY(),
                                () -> -controller.getLeftX(),
                                () -> Rotation2d.kZero));

        // Switch to X pattern when X button is pressed
        controller.x()
                .onTrue(new ActionFactory().singleAction("X-Command", () -> drive.stopWithX(), drive));

        // Reset gyro to 0° when B button is pressed
        controller.b()
                .onTrue(new ActionFactory().singleAction("ZeroGyroCommand",
                        () -> drive.setPose(new Pose2d(drive.getPose().getTranslation(), Rotation2d.kZero)),
                        drive).ignoringDisable(true));

        // Antitipping
        controller.leftBumper()
                .whileTrue(new ActionFactory().continuousAction("DriveWithAntiTipping",
                        () -> DriveCommands.joystickDriveWithAntiTipping(drive, () -> -controller.getLeftY(),
                                () -> -controller.getLeftX(), () -> -controller.getRightX(), antiTipping),
                        () -> DriveCommands.joystickDriveWithAntiTipping(drive, () -> 0, () -> 0, () -> 0,
                                antiTipping)));

        operator.leftBumper().whileTrue(Commands.run(() -> {
            fuel.setIntakeLauncherRoller(FuelConstants.SHOOTER_INTAKE_PERCENT);
            fuel.setFeederRoller(FuelConstants.FEEDER_INTAKING_PERCENT);
        }, fuel)).onFalse(Commands.runOnce(() -> fuel.stop(), fuel));
        operator.rightBumper().whileTrue(Commands.run(() -> {
            shooter.setIntakeLauncherRoller(ShooterConstants.SHOOTER_INTAKE_PERCENT);
            shooter.setFeederRoller(ShooterConstants.FEEDER_INTAKING_PERCENT);
        }, shooter).withTimeout(ShooterConstants.SPIN_UP_SECONDS).andThen(Commands.run(() -> {
            shooter.setIntakeLauncherRoller(ShooterConstants.SHOOTER_INTAKE_PERCENT);
            shooter.setFeederRoller(ShooterConstants.FEEDER_EJECT_PERCENT);
        }, shooter))).onFalse(Commands.runOnce(() -> shooter.stop(), shooter));
         operator.a().whileTrue(Commands.run(() -> {
            fuel.setIntakeLauncherRoller(FuelConstants.SHOOTER_INTAKE_EJECT_PERCENT);
            fuel.setFeederRoller(FuelConstants.FEEDER_EJECT_PERCENT);
        }, fuel)).onFalse(Commands.runOnce(() -> fuel.stop(), fuel));
         operator.povUp().whileTrue(Commands.run(() -> {
            climber.setClimber(ClimbConstatns.CLIMBER_MOTOR_UP_PERCENT);
        }, climber)).onFalse(Commands.runOnce(() -> climber.stop(), climber));
   operator.povDown().whileTrue(Commands.run(() -> {
            climber.setClimber(ClimbConstatns.CLIMBER_MOTOR_DOWN_PERCENT);
        }, climber)).onFalse(Commands.runOnce(() -> climber.stop(), climber));
    }

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return the command to run in autonomous
     */
    public Command getAutonomousCommand() {
        SequentialCommandGroup Shooter = new SequentialCommandGroup(Commands.run(() -> {
            shooter.setIntakeLauncherRoller(FuelConstants.SHOOTER_INTAKE_PERCENT);
        Commands.waitSeconds(2.5); 
        shooter.setFeederRoller(ShooterConstants.FEEDER_INTAKING_PERCENT);}));
        return autoChooser.get();
    }

    public Pose2d getPose2D() {
        return drive.getPose();
    }

    public void teleopPeriodic() {
        antiTipping.calculate();
        HubData hubData = hub.getHubData();
        Logger.recordOutput("Hub/Status", hubData.owner);
        Logger.recordOutput("Hub/TimeRemaing", hubData.timeRemaining);
    }

    
    }
