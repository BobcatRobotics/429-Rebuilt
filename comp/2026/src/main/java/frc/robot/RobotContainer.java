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
import edu.wpi.first.wpilibj.smartdashboard.*;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.commands.PathPlannerAuto;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
// import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import frc.robot.Constants.ClimbConstatns;
import frc.robot.Constants.IntakeConstants;
import frc.robot.Constants.ShooterConstants;
import frc.robot.commands.Blue_Simple_Auto;
import frc.robot.commands.DriveCommands;
import frc.robot.commands.IntakeCommands;
import frc.robot.commands.ShooterCommands;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.climber.Climber;
import frc.robot.subsystems.climber.ClimberIO;
import frc.robot.subsystems.climber.ClimberIOReal;
import frc.robot.subsystems.climber.ClimberIOSim;
import frc.robot.subsystems.drive.Drive;
import frc.robot.subsystems.drive.GyroIO;
import frc.robot.subsystems.drive.GyroIOPigeon2;
import frc.robot.subsystems.drive.ModuleIO;
import frc.robot.subsystems.drive.ModuleIOSim;
import frc.robot.subsystems.drive.ModuleIOTalonFX;
import frc.robot.subsystems.fuel.Fuel;
import frc.robot.subsystems.fuel.FuelIO;
import frc.robot.subsystems.fuel.FuelIOReal;
import frc.robot.subsystems.fuel.FuelIOSim;
import frc.robot.subsystems.vision.Vision;
import frc.robot.subsystems.vision.VisionIOLimelight;
import frc.robot.util.AllianceFlipUtil;
import frc.robot.commands.SimpleAuto;
import frc.robot.commands.SimpleAuto_Climb_Blue;
import frc.robot.commands.SimpleAuto_Climb_Red;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.math.geometry.Translation2d;
import frc.robot.subsystems.vision.VisionConstants;

import static frc.robot.Constants.IntakeConstants.INTAKE_PERCENT;

import org.bobcatrobotics.Commands.ActionFactory;
import org.bobcatrobotics.GameSpecific.Rebuilt.HubData;
import org.bobcatrobotics.GameSpecific.Rebuilt.HubUtil;
import org.bobcatrobotics.Subsystems.AntiTippingLib.AntiTipping;
import org.bobcatrobotics.Subsystems.Swerve.ModuleWrapper;

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
    private final Fuel fuel;
    private final Climber climber;
    public final Drive drive;
    private final AntiTipping antiTipping;
    private Vision vision;

    // Controller
    private final CommandXboxController driver = new CommandXboxController(0);
    private final CommandXboxController operator = new CommandXboxController(1);

    // Dashboard inputs
    private final SendableChooser<Command> autoChooser;

    private final HubUtil hub;
    
    /**
     * The container for the robot. Contains subsystems, OI devices, and commands.
     */
    public RobotContainer() {
        ModuleWrapper newFrontRight = new ModuleWrapper("FrontRight.json", "FrontRight");
        ModuleWrapper newFrontLeft = new ModuleWrapper("FrontLeft.json", "FrontLeft");
        ModuleWrapper newBackLeft = new ModuleWrapper("BackLeft.json", "BackLeft");
        ModuleWrapper newBackRight = new ModuleWrapper("BackRight.json", "BackRight");
        switch (Constants.currentMode) {
            case REAL:
                // Real robot, instantiate hardware IO implementations
                drive = new Drive(new GyroIOPigeon2(),
                        new ModuleIOTalonFX(newFrontLeft.addModuleConstants(TunerConstants.FrontLeft)),
                        new ModuleIOTalonFX(newFrontRight.addModuleConstants(TunerConstants.FrontRight)),
                        new ModuleIOTalonFX(newBackLeft.addModuleConstants(TunerConstants.BackLeft)),
                        new ModuleIOTalonFX(newBackRight.addModuleConstants(TunerConstants.BackRight)));
                fuel = new Fuel(new FuelIOReal());
                climber = new Climber(new ClimberIOReal());
                // Vision
                vision =
                 new Vision(
                    drive::addVisionMeasurement,
                    new VisionIOLimelight(VisionConstants.camera0Name, drive::getRotation));
                break;
            case SIM:
                // Sim robot, instantiate physics sim IO implementations
                drive = new Drive(new GyroIO() {
                }, new ModuleIOSim(TunerConstants.FrontLeft),
                        new ModuleIOSim(TunerConstants.FrontRight), new ModuleIOSim(TunerConstants.BackLeft),
                        new ModuleIOSim(TunerConstants.BackRight));
                fuel = new Fuel(new FuelIOSim());
                climber = new Climber(new ClimberIOSim());
                vision =
                 new Vision(
                    drive::addVisionMeasurement,
                    new VisionIOLimelight(VisionConstants.camera0Name, drive::getRotation));
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
                fuel = new Fuel(new FuelIO() {
                });
                climber = new Climber(new ClimberIO() {
                });
                vision =
                 new Vision(
                    drive::addVisionMeasurement,
                    new VisionIOLimelight(VisionConstants.camera0Name, drive::getRotation));
                break;
        }

        antiTipping = new AntiTipping(() -> drive.getPitch(), () -> drive.getRoll(), 0.04, // kP
                3.0, // tipping threshold (degrees)
                2.5 // max correction speed (m/s)
        );

        // Set up auto routines

        registerNammedCommands();

        autoChooser = new SendableChooser<>();

        // autoChooser.addOption("Drive back and Shoot", new SimpleAuto(drive));
        // autoChooser.addOption("Drive Back Shoot with Climb Blue", new SimpleAuto_Climb_Blue(drive));
        // autoChooser.addOption("Drive back and Shoot Blue Side", new Blue_Simple_Auto(drive));
        // autoChooser.addOption("Drive back and Shoot with Climb Red Side", new SimpleAuto_Climb_Red(drive));

        //23 means 8 shot plus a climb
        //double tower means go to tower first and shoot then depot and back to tower for another shot

        autoChooser.addOption("Akash Scoot and Shoot", new PathPlannerAuto("Akash Scoot and Shoot"));
        autoChooser.addOption("Hub to Tower Shoot", new PathPlannerAuto("Hub to Tower shoot"));
        autoChooser.addOption("Hub 23", new PathPlannerAuto("Hub to Tower Shoot + Climb"));
        autoChooser.addOption("Left bump 23", new PathPlannerAuto("Left Bump to shoot and climb"));
        autoChooser.addOption("Right bump 23", new PathPlannerAuto("Right Bump to shoot and climb"));
        autoChooser.addOption("Hub double tower and climb", new PathPlannerAuto("Hub start with double tower shot and climb"));
        autoChooser.addOption("Left bump double tower and climb", new PathPlannerAuto("Left bump start with double tower shot and climb"));
        autoChooser.addOption("Right bump double tower and climb", new PathPlannerAuto("Right bump start with double tower shot and climb"));
        
        SmartDashboard.putData("Auto Chooser", autoChooser);

            // Set up SysId routines
//     autoChooser.addOption(
//         "Drive Wheel Radius Characterization", DriveCommands.wheelRadiusCharacterization(drive));
//     autoChooser.addOption(
//         "Drive Simple FF Characterization", DriveCommands.feedforwardCharacterization(drive));
//     autoChooser.addOption(
//         "Drive SysId (Quasistatic Forward)",
//         drive.sysIdQuasistatic(SysIdRoutine.Direction.kForward));
//     autoChooser.addOption(
//         "Drive SysId (Quasistatic Reverse)",
//         drive.sysIdQuasistatic(SysIdRoutine.Direction.kReverse));
//     autoChooser.addOption(
//         "Drive SysId (Dynamic Forward)", drive.sysIdDynamic(SysIdRoutine.Direction.kForward));
//     autoChooser.addOption(
//         "Drive SysId (Dynamic Reverse)", drive.sysIdDynamic(SysIdRoutine.Direction.kReverse));

        // Configure the button bindings
        configureButtonBindings();

        hub = new HubUtil();
    }

    private void registerNammedCommands(){
        NamedCommands.registerCommand("Intake", Commands.run(() -> {
            fuel.setIntakePower(IntakeConstants.INTAKE_PERCENT);
            fuel.setFeederRoller(IntakeConstants.FEEDER_INTAKING_PERCENT);
        }, fuel)
            .withTimeout(3));

        NamedCommands.registerCommand("Shooter at tower distance", Commands.run(() -> {
            fuel.setShooterRightPower(ShooterConstants.SHOOTER_PERCENT);
            fuel.setFeederRoller(ShooterConstants.FEEDER_INTAKING_PERCENT);
            fuel.setIntakePower(IntakeConstants.INTAKE_PERCENT);
        }, fuel)
            .withTimeout(ShooterConstants.SPIN_UP_SECONDS)
            .andThen(Commands.run(() -> {
                fuel.setShooterRightPower(ShooterConstants.SHOOTER_PERCENT);
                fuel.setFeederRoller(ShooterConstants.FEEDER_EJECT_PERCENT);
            }, fuel))
                .withTimeout(3.5));

        NamedCommands.registerCommand("Climb down", (Commands.run(() -> {
            climber.setClimberPower(ClimbConstatns.CLIMBER_AUTO_DOWN_PERCENT);
        }, climber)
            .withTimeout(0.7)));

        NamedCommands.registerCommand("Pre Climb Auto Set Up", Commands.run(() -> {
            climber.setClimberPower(ClimbConstatns.CLIMBER_AUTO_DOWN_PERCENT);
        }, climber)
            .withTimeout(2.3));

        NamedCommands.registerCommand("Stop Climber", (Commands.runOnce(() -> {
            climber.stop();
        }, climber)));

        NamedCommands.registerCommand("Stop Shooting", Commands.runOnce(() -> {
            fuel.stop();
        }, fuel));

        NamedCommands.registerCommand("Shooter spin", Commands.run(() -> {
            fuel.setShooterRightPower(ShooterConstants.SHOOTER_PERCENT);
            fuel.setShooterLeftPower(ShooterConstants.SHOOTER_PERCENT);
            fuel.setFeederRoller(ShooterConstants.FEEDER_EJECT_PERCENT);
            fuel.setIntakePower(IntakeConstants.INTAKE_PERCENT);
        }, fuel)
            .withTimeout(1));

        NamedCommands.registerCommand("Climb Up", Commands.run(() -> {
            climber.setClimberPower(ClimbConstatns.CLIMBER_MOTOR_UP_PERCENT);
        }, climber)
            .withTimeout(4));

        NamedCommands.registerCommand("Intake stop", Commands.runOnce(() -> {
            fuel.stop();
        }));
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
                        () -> -driver.getLeftY(),
                        () -> -driver.getLeftX(),
                        () -> -driver.getRightX()));

        fuel.setDefaultCommand(fuel.run(() -> fuel.stop()));
        climber.setDefaultCommand(climber.run(() -> climber.stop()));

        // Switch to X pattern when X button is pressed
        driver.x()
                .onTrue(new ActionFactory().singleAction("X-Command", () -> drive.stopWithX(), drive));

        // Reset gyro / field orientation when B button is pressed
        //Akash changed this to what 177 uses
                // Reset gyro to 0° when B button is pressed
                driver.b()
                                .onTrue(new ActionFactory().singleAction("ZeroGyroCommand",
                                                () -> drive.setPose(new Pose2d(drive.getPose().getTranslation(),
                                                                AllianceFlipUtil.apply(Rotation2d.kZero))),
                                                drive).ignoringDisable(true));
            driver.a().whileTrue(
            DriveCommands.joystickDriveAtAngle(
                         drive,
                        () -> -driver.getLeftY(),
                        () -> -driver.getLeftX(),
                        () -> new Rotation2d(Constants.hubLocation.getX()-drive.getPose().getX(), Constants.hubLocation.getY()-drive.getPose().getY())));

        //intake
        operator.leftBumper().whileTrue(Commands.run(() -> {
            fuel.setIntakePower(IntakeConstants.INTAKE_PERCENT);
            fuel.setFeederRoller(IntakeConstants.FEEDER_INTAKING_PERCENT);
        }, fuel)).onFalse(Commands.runOnce(() -> fuel.stop(), fuel));

        //shoot
          operator.rightBumper().whileTrue(Commands.run(() -> {
            climber.setClimberPower(ClimbConstatns.CLIMBER_MOTOR_DOWN_PERCENT);
            fuel.setShooterRightPower(ShooterConstants.SHOOTER_PERCENT);
            fuel.setFeederRoller(ShooterConstants.FEEDER_INTAKING_PERCENT);
            fuel.setIntakePower(IntakeConstants.INTAKE_PERCENT);
        }, fuel).withTimeout(ShooterConstants.SPIN_UP_SECONDS).andThen(Commands.run(() -> {
            fuel.setShooterRightPower(ShooterConstants.SHOOTER_PERCENT);
            fuel.setFeederRoller(ShooterConstants.FEEDER_EJECT_PERCENT);
        }).withTimeout(0.5).andThen(Commands.run(() -> {
            fuel.setShooterRightPower(ShooterConstants.SHOOTER_LONG_PERCENT);
            fuel.setFeederRoller(ShooterConstants.FEEDER_EJECT_PERCENT);
        }))));

            operator.rightBumper().onFalse(Commands.run(() -> {
                fuel.setShooterRightPower(ShooterConstants.SHOOTER_PERCENT);
            }, fuel).withTimeout(1).andThen(Commands.runOnce(() -> fuel.setShooterRightPower(ShooterConstants.SHOOTER_STOP_PERCENT))));

        operator.y().whileTrue(Commands.run(() -> {
            climber.setClimberPower(ClimbConstatns.CLIMBER_MOTOR_DOWN_PERCENT);
            fuel.setShooterRightPower(ShooterConstants.SHOOTER_PERCENT_MID);
            fuel.setFeederRoller(ShooterConstants.FEEDER_INTAKING_PERCENT);
            fuel.setIntakePower(IntakeConstants.INTAKE_PERCENT);
        }, fuel).withTimeout(ShooterConstants.SPIN_UP_SECONDS).andThen(Commands.run(() -> {
            fuel.setShooterRightPower(ShooterConstants.SHOOTER_PERCENT_MID);
            fuel.setFeederRoller(ShooterConstants.FEEDER_EJECT_PERCENT);
        })));

        operator.y().onFalse(Commands.run(() -> {
                fuel.setShooterRightPower(ShooterConstants.SHOOTER_PERCENT_MID);
            }, fuel).withTimeout(1).andThen(Commands.runOnce(() -> fuel.setShooterRightPower(ShooterConstants.SHOOTER_STOP_PERCENT))));

        operator.x().whileTrue(Commands.run(() -> {
            climber.setClimberPower(ClimbConstatns.CLIMBER_MOTOR_DOWN_PERCENT);
            fuel.setShooterRightPower(ShooterConstants.SHOOTER_PERCENT_CLOSE);
            fuel.setFeederRoller(ShooterConstants.FEEDER_INTAKING_PERCENT);
            fuel.setIntakePower(IntakeConstants.INTAKE_PERCENT);
        }, fuel).withTimeout(ShooterConstants.SPIN_UP_SECONDS).andThen(Commands.run(() -> {
            fuel.setShooterRightPower(ShooterConstants.SHOOTER_PERCENT_CLOSE);
            fuel.setFeederRoller(ShooterConstants.FEEDER_EJECT_PERCENT);
        })));

        operator.x().onFalse(Commands.run(() -> {
                fuel.setShooterRightPower(ShooterConstants.SHOOTER_PERCENT_CLOSE);
            }, fuel).withTimeout(1).andThen(Commands.runOnce(() -> fuel.setShooterRightPower(ShooterConstants.SHOOTER_STOP_PERCENT))));
            

        //eject through intake
       operator.a().whileTrue(Commands.run(() -> {
            fuel.setShooterRightPower(ShooterConstants.SHOOTER_EJECT_PERCENT);
            fuel.setIntakePower(IntakeConstants.INTAKE_EJECT_PERCENT);
            fuel.setFeederRoller(IntakeConstants.FEEDER_EJECT_PERCENT);
        }, fuel)).onFalse(Commands.runOnce(() -> fuel.stop(), fuel));

        //climb up
        operator.povUp().whileTrue(Commands.run(() -> {
            climber.setClimberPower(ClimbConstatns.CLIMBER_MOTOR_UP_PERCENT);
        }, climber)).onFalse(Commands.runOnce(() -> climber.stop(), climber));

        //climb down
        operator.povDown().whileTrue(Commands.run(() -> {
            climber.setClimberPower(ClimbConstatns.CLIMBER_MOTOR_DOWN_PERCENT);
        }, climber)).onFalse(Commands.runOnce(() -> climber.stop(), climber));

        operator.start().whileTrue(climber.disableLimits());

        operator.start().onFalse(climber.enableLimits());

        // operator.back().onFalse(climber.enableLimits().andThen(Commands.run(() -> {
        //     climber.setClimberPower(ClimbConstatns.CLIMBER_MOTOR_UP_PERCENT);
        // })));
        
    }

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return the command to run in autonomous
     */
    public Command getAutonomousCommand() {
        return autoChooser.getSelected();
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