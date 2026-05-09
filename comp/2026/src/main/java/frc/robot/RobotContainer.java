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
import com.pathplanner.lib.path.PathConstraints;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
// import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import frc.robot.Constants.ClimbConstants;
import frc.robot.Constants.IntakeConstants;
import frc.robot.Constants.ShooterConstants;
import frc.robot.commands.ClimberCommands;
import frc.robot.commands.DriveCommands;
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
import edu.wpi.first.math.util.Units;
import frc.robot.subsystems.vision.VisionConstants;

import java.util.Set;

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
    private final SendableChooser<Boolean> climbLocationChooser;
    
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
                    new VisionIOLimelight(VisionConstants.camera0Name, drive::getRotation),
                    new VisionIOLimelight(VisionConstants.camera1Name, drive::getRotation));
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
                    new VisionIOLimelight(VisionConstants.camera0Name, drive::getRotation),
                    new VisionIOLimelight(VisionConstants.camera1Name, drive::getRotation));
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
                    new VisionIOLimelight(VisionConstants.camera0Name, drive::getRotation),
                    new VisionIOLimelight(VisionConstants.camera1Name, drive::getRotation));
                break;
        }

        antiTipping = new AntiTipping(() -> drive.getPitch(), () -> drive.getRoll(), 0.04, // kP
                3.0, // tipping threshold (degrees)
                2.5 // max correction speed (m/s)
        );
        
        climbLocationChooser = new SendableChooser<>();
        climbLocationChooser.setDefaultOption("left tower climb", true);
        climbLocationChooser.addOption("right tower climb", false);

        SmartDashboard.putData("Climb Location Chooser", climbLocationChooser);

        // Set up auto routines

        registerNammedCommands();

        autoChooser = new SendableChooser<>();

        // autoChooser.addOption("Drive back and Shoot", new SimpleAuto(drive));
        // autoChooser.addOption("Drive Back Shoot with Climb Blue", new SimpleAuto_Climb_Blue(drive));
        // autoChooser.addOption("Drive back and Shoot Blue Side", new Blue_Simple_Auto(drive));
        // autoChooser.addOption("Drive back and Shoot with Climb Red Side", new SimpleAuto_Climb_Red(drive));

        //autoChooser.addOption("Hub to Tower Shoot", new PathPlannerAuto("Hub to Tower shoot"));
        autoChooser.addOption("Hub to Depot shoot and climb", new PathPlannerAuto("Hub to Depot shoot and climb"));
        autoChooser.addOption("Left Bump Shoot Mid Shoot", new PathPlannerAuto("Left Bump Shoot Mid Shoot"));
        autoChooser.addOption("Left Bump to Depot shoot and climb", new PathPlannerAuto("Left Bump to Depot shoot and climb"));

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
    }

    private void registerNammedCommands(){
        NamedCommands.registerCommand("Intake", Commands.runOnce(() -> {
            fuel.setIntakePower(IntakeConstants.INTAKE_PERCENT);
            fuel.setFeederRoller(IntakeConstants.FEEDER_INTAKING_PERCENT);
        }, fuel));

        NamedCommands.registerCommand("Shoot", Commands.run(() -> {
            fuel.setShooterRightVelocity(RobotState.getInstance().getShooterVelocity());
            fuel.setFeederRoller(ShooterConstants.FEEDER_INTAKING_PERCENT);
            fuel.setIntakePower(IntakeConstants.INTAKE_PERCENT);
        }, fuel)
            .withTimeout(ShooterConstants.SPIN_UP_AUTO_SECONDS)
            .andThen(Commands.runOnce(() -> {
                fuel.setShooterRightVelocity(RobotState.getInstance().getShooterVelocity());
                fuel.setFeederRoller(ShooterConstants.FEEDER_EJECT_PERCENT);
            }, fuel)));

        NamedCommands.registerCommand("Climb down", (Commands.runOnce(() -> {
            climber.setClimberPower(ClimbConstants.CLIMBER_AUTO_DOWN_PERCENT);
        }, climber)));

        NamedCommands.registerCommand("Stop Climber", (Commands.runOnce(() -> {
            climber.stop();
        }, climber)));

        NamedCommands.registerCommand("Stop Shooting", Commands.runOnce(() -> {
            fuel.stop();
        }, fuel));
       
        NamedCommands.registerCommand("Intake stop", Commands.runOnce(() -> {
            fuel.stop();
        }));

        NamedCommands.registerCommand("Auto Climb", Commands.defer(() -> ClimberCommands.climbToLevel(drive, climber, climbLocationChooser.getSelected(), ClimbConstants.CLIMBER_CLIMBED_PITCH_L1), Set.of(drive)));
    }
    /**
     * Use this method to define your button->command mappings. Buttons can be
     * created by
     * instantiating a {@link GenericHID} or one of its subclasses
     * ({@link edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then
     * passing it to a
     * {@link edu.wpi.first.wpilibj2.command.button.JoystickButton}.
     * 
     * The button visual configuration are maintained here. If you update/add buttons, then ensure to change the URL below
     * https://www.padcrafter.com/index.php?col=%23242424%2C%23606A6E%2C%23FFFFFF&outline=0&templates=Driver%7COperator&plat=0&timestamp=1778345932804&xButton=Set+drivetrain+to+X+mode%7CClose+Distance+Shot&bButton=Reset+gyro+to+zero%7C&rightBumper=Align+to+hub%7CShoot&dpadLeft=Auto+climb+left+side%7C&dpadRight=Auto+climb+right+side%7C&leftBumper=%7CIntake&yButton=Stop+all+commands%7CTower+Distance+Shot&dpadUp=%7CManual+Climb+Up&dpadDown=%7CManual+Climb+Down&startButton=%7CDisable+Soft+Limits&backButton=%7CReset+climb+position+to+zero&aButton=%7CEject%2FOuttake
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
        driver.b()
            .onTrue(new ActionFactory().singleAction("ZeroGyroCommand",
                            () -> drive.setPose(new Pose2d(drive.getPose().getTranslation(),
                                            AllianceFlipUtil.apply(Rotation2d.kZero))),
                            drive).ignoringDisable(true));

        //align to hub
        driver.rightBumper().whileTrue(
            DriveCommands.joystickDriveAtAngle(
                    drive,
                    () -> -driver.getLeftY(),
                    () -> -driver.getLeftX(),
                    () -> new Rotation2d(RobotState.getInstance().hubLocation.getX()-drive.getPose().getX(), RobotState.getInstance().hubLocation.getY()-drive.getPose().getY())));

        //drive to tower and climb left side
        driver.povLeft().onTrue(ClimberCommands.climbToLevel(drive, climber, true, ClimbConstants.CLIMBER_CLIMBED_PITCH_L2));
        
        //drive to tower and climb right side
        driver.povRight().onTrue(ClimberCommands.climbToLevel(drive, climber, false, ClimbConstants.CLIMBER_CLIMBED_PITCH_L2));

        // for stopping all commands
        driver.y().onTrue(Commands.runOnce(() -> CommandScheduler.getInstance().cancelAll())
                .andThen(Commands.runOnce(() -> climber.stop())));

        //intake
        operator.leftBumper().whileTrue(Commands.run(() -> {
            fuel.setIntakePower(IntakeConstants.INTAKE_PERCENT);
            fuel.setFeederRoller(IntakeConstants.FEEDER_INTAKING_PERCENT);
        }, fuel)).onFalse(Commands.runOnce(() -> fuel.stop(), fuel));

        //shoot
        operator.rightBumper().whileTrue(Commands.run(() -> {
            climber.setClimberPower(ClimbConstants.CLIMBER_MOTOR_DOWN_PERCENT);
            fuel.setShooterRightVelocity(RobotState.getInstance().getShooterVelocity());
            fuel.setFeederRoller(ShooterConstants.FEEDER_INTAKING_PERCENT);
            fuel.setIntakePower(IntakeConstants.INTAKE_PERCENT);
        }, fuel).withTimeout(ShooterConstants.SPIN_UP_SECONDS).andThen(Commands.run(() -> {
            fuel.setShooterRightVelocity(RobotState.getInstance().getShooterVelocity());
            fuel.setFeederRoller(ShooterConstants.FEEDER_EJECT_PERCENT);
        }).withTimeout(0.5).andThen(Commands.run(() -> {
            fuel.setShooterRightVelocity(RobotState.getInstance().getShooterVelocity());
            fuel.setFeederRoller(ShooterConstants.FEEDER_EJECT_PERCENT);
        }))));

        // stop shooting after 1 second
        operator.rightBumper().onFalse(Commands.run(() -> {
            fuel.setShooterRightVelocity(RobotState.getInstance().getShooterVelocity());
        }, fuel).withTimeout(1).andThen(Commands.runOnce(() -> fuel.setShooterRightVelocity(ShooterConstants.SHOOTER_STOP_PERCENT))));

        // Manual Tower shot
        operator.y().whileTrue(Commands.run(() -> {
            climber.setClimberPower(ClimbConstants.CLIMBER_MOTOR_DOWN_PERCENT);
            fuel.setShooterRightVelocity(ShooterConstants.SHOOTER_PERCENT_TOWER);
            fuel.setFeederRoller(ShooterConstants.FEEDER_INTAKING_PERCENT);
            fuel.setIntakePower(IntakeConstants.INTAKE_PERCENT);
        }, fuel).withTimeout(ShooterConstants.SPIN_UP_SECONDS).andThen(Commands.run(() -> {
            fuel.setShooterRightVelocity(ShooterConstants.SHOOTER_PERCENT_TOWER);
            fuel.setFeederRoller(ShooterConstants.FEEDER_EJECT_PERCENT);
        })));

        // Stop Manual Tower shot after 1 second
        operator.y().onFalse(Commands.run(() -> {
            fuel.setShooterRightVelocity(ShooterConstants.SHOOTER_PERCENT_TOWER);
        }, fuel).withTimeout(1).andThen(Commands.runOnce(() -> fuel.setShooterRightVelocity(ShooterConstants.SHOOTER_STOP_PERCENT))));

        // Manual Close shot
        operator.x().whileTrue(Commands.run(() -> {
            climber.setClimberPower(ClimbConstants.CLIMBER_MOTOR_DOWN_PERCENT);
            fuel.setShooterRightVelocity(ShooterConstants.SHOOTER_PERCENT_CLOSE);
            fuel.setFeederRoller(ShooterConstants.FEEDER_INTAKING_PERCENT);
            fuel.setIntakePower(IntakeConstants.INTAKE_PERCENT);
        }, fuel).withTimeout(ShooterConstants.SPIN_UP_SECONDS).andThen(Commands.run(() -> {
            fuel.setShooterRightVelocity(ShooterConstants.SHOOTER_PERCENT_CLOSE);
            fuel.setFeederRoller(ShooterConstants.FEEDER_EJECT_PERCENT);
        })));

        // Stop Manual Close shot after 1 second
        operator.x().onFalse(Commands.run(() -> {
            fuel.setShooterRightVelocity(ShooterConstants.SHOOTER_PERCENT_CLOSE);
        }, fuel).withTimeout(1).andThen(Commands.runOnce(() -> fuel.setShooterRightVelocity(ShooterConstants.SHOOTER_STOP_PERCENT))));
            

        //eject through intake
       operator.a().whileTrue(Commands.run(() -> {
            fuel.setShooterRightVelocity(ShooterConstants.SHOOTER_EJECT_VELOCITY);
            fuel.setIntakePower(IntakeConstants.INTAKE_EJECT_PERCENT);
            fuel.setFeederRoller(IntakeConstants.FEEDER_EJECT_PERCENT);
        }, fuel)).onFalse(Commands.runOnce(() -> fuel.stop(), fuel));

        //climb up
        operator.povUp().whileTrue(Commands.run(() -> {
            climber.setClimberPower(ClimbConstants.CLIMBER_MOTOR_UP_PERCENT);
        }, climber)).onFalse(Commands.runOnce(() -> climber.stop(), climber));

        //climb down
        operator.povDown().whileTrue(Commands.run(() -> {
            climber.setClimberPower(ClimbConstants.CLIMBER_MOTOR_DOWN_PERCENT);
        }, climber)).onFalse(Commands.runOnce(() -> climber.stop(), climber));

        // while held ignore climb soft limits
        operator.start().whileTrue(climber.disableLimits());

        operator.start().onFalse(climber.enableLimits());

        // set Climber position to 0
        operator.back().onTrue(Commands.runOnce(() -> climber.setClimberZero(), climber).ignoringDisable(true));
        
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
        HubData hubData = HubUtil.getHubData();
        Logger.recordOutput("Hub/Status", hubData.owner);
        Logger.recordOutput("Hub/TimeRemaing", hubData.timeRemaining);
    }
}