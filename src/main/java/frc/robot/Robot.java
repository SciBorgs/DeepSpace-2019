package frc.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import frc.robot.controlscheme.ControlScheme;
import frc.robot.controlscheme.XboxControl;
import frc.robot.subsystems.*;
import frc.robot.commands.*;
import frc.robot.helpers.*;
import frc.robot.logging.*;
import frc.robot.logging.Logger.DefaultValue;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.DigitalOutput;
import frc.robot.Utils;

// FILE HAS NOT BEEN CLEANED UP //
public class Robot extends TimedRobot {
    public static Logger logger = new Logger();
    public static OI oi = new OI();
    
    public static IntakeSubsystem     intakeSubsystem     = new IntakeSubsystem();
    public static DriveSubsystem      driveSubsystem      = new DriveSubsystem();
    public static EncoderSubsystem    encoderSubsystem    = new EncoderSubsystem();
    public static RobotPosition       robotPosition       = new RobotPosition();
    public static LiftSubsystem       liftSubsystem       = new LiftSubsystem();
    public static GearShiftSubsystem  gearShiftSubsystem  = new GearShiftSubsystem();
    public static LimelightSubsystem  limelightSubsystem  = new LimelightSubsystem();
    public static PneumaticsSubsystem pneumaticsSubsystem = new PneumaticsSubsystem();
    
    public static Following following = new Following();
    public static Lineup    lineup    = new Lineup();
    
    private final ControlScheme xboxControl    = new XboxControl();
    private final PowerDistributionPanel pdp   = new PowerDistributionPanel();
    private final DigitalOutput targetingLight = new DigitalOutput(5);

    private boolean lightOff = true;
    private boolean prevLightButton = false;
    private int attemptsSinceLastLog;
    
    public static double MANUAL_CASCADE_INPUT  = 1;
    public static double MANUAL_CARRIAGE_INPUT = .55;
    public static final int LOG_PERIOD = 5;

    private void allPeriodicLogs() {
        driveSubsystem.periodicLog();
        gearShiftSubsystem.periodicLog();
        intakeSubsystem.periodicLog();
        limelightSubsystem.periodicLog();
        pneumaticsSubsystem.periodicLog();
        encoderSubsystem.periodicLog();
        liftSubsystem.periodicLog();
        lineup.periodicLog();
        following.periodicLog();
    }

    private CANSparkMax[] getSparks() {
        CANSparkMax[] driveSparksArray = driveSubsystem.getSparks();
        CANSparkMax[] liftSparksArray  = liftSubsystem.getSparks();
        CANSparkMax[] allSparks = Utils.combineArray(driveSparksArray, liftSparksArray);
        return allSparks;
    }

    private TalonSRX[] getTalons() {
        TalonSRX[] positionTalonArray = robotPosition.getTalons();
        TalonSRX[] liftTalonArray     = liftSubsystem.getTalons();
        TalonSRX[] intakeTalonArray   = intakeSubsystem.getTalons();
        TalonSRX[] allTalons = Utils.combineArray(positionTalonArray, Utils.combineArray(liftTalonArray, intakeTalonArray));
        return allTalons;
    }

    public void robotInit() {
        xboxControl.getShuffleboardCommand(pdp, liftSubsystem, pneumaticsSubsystem, getSparks(), getTalons(), liftSubsystem.getLiftSpark(), following.getPid(), driveSubsystem.getTankAnglePID(), driveSubsystem.getMaxOmegaGoal(), liftSubsystem.getArmPID(), liftSubsystem.getLiftPID(), lineup.getShiftPID()).start();
        attemptsSinceLastLog = 0;
        robotPosition.updatePositionTank();
        pneumaticsSubsystem.stopCompressor();
        intakeSubsystem.closeArm();
        following.modeToCargo();
        logger.incrementPrevious("robot.java", "deploy", DefaultValue.Previous);

        /* STARTS THE LIDAR     
        try {
            System.out.println("LIDAR status: starting");
            boolean started = LidarServer.getInstance().start();
            System.out.println("LIDAR status" + (started ? "started" : "failed to start"));
        } catch (Throwable t) {
            System.out.println("LIDAR status: crashed -" + t);
            t.printStackTrace();
            throw t;
        }*/

        logger.logData();
    }

    public void logDataPeriodic() {
        if (LOG_PERIOD == attemptsSinceLastLog) {
            attemptsSinceLastLog = 0;
            logger.logData();
        } else {
            attemptsSinceLastLog++;
        }
    }
 
    public void robotPeriodic() {
        Scheduler.getInstance().run();
        robotPosition.updatePositionTank();
    }
        
    public void autonomousInit() {
        following.modeToCargo();
        (new LiftCommand()).start();
    }

    public void autonomousPeriodic() {
        new SwerveTankDriveCommand().start();
        manualCascade();
    }
    
    @Override
    public void teleopInit() {
        liftSubsystem.setLiftSpeed(0);
        (new LiftCommand()).start();
    }

    public void manualCascade() {
        if(Robot.oi.leftStick.getPOV() == 0) {
            Robot.liftSubsystem.setLiftSpeed(MANUAL_CASCADE_INPUT);
        }else if(Robot.oi.leftStick.getPOV() == 180) {
            Robot.liftSubsystem.setLiftSpeed(-MANUAL_CASCADE_INPUT);
        }else if(liftSubsystem.manualCascadeMode) {
            Robot.liftSubsystem.setLiftSpeed(0);
        }
    }

    public void teleopPeriodic() {
        manualCascade();
        new TankDriveCommand().start();

        boolean targetLightButton = oi.xboxController.getBButton();

        if(targetLightButton && !prevLightButton && lightOff) {
            targetingLight.set(true);
            lightOff = false;
        } else {
            targetingLight.set(false);
            lightOff = true;
        }
        prevLightButton = targetLightButton;
        
        pneumaticsSubsystem.startCompressor();
    }

    public void testPeriodic() {
        liftSubsystem.moveToInitial();
    }

    public void disabledInit() {}
}