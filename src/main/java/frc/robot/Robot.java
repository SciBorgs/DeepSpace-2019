package frc.robot;

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

    private boolean lightOn = false;
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

    public void robotInit() {
        xboxControl.getShuffleboardCommand(pdp, liftSubsystem, pneumaticsSubsystem, liftSubsystem.getLiftSpark(), following.getPid(), driveSubsystem.getTankAnglePID(), driveSubsystem.getMaxOmegaGoal(), liftSubsystem.getArmPID(), liftSubsystem.getLiftPID(), lineup.getShiftPID()).start();
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
        //conditions to turn light on
        boolean toggleLight = !this.prevLightButton && targetLightButton;             
        if(toggleLight && !this.lightOn) {
            this.targetingLight.set(toggleLight);
            this.lightOn = toggleLight;
        } else if (toggleLight) {
            this.targetingLight.set(!toggleLight);
            this.lightOn = !toggleLight;
        }
        this.prevLightButton = targetLightButton;
        
        pneumaticsSubsystem.startCompressor();
    }

    public void testPeriodic() {
        liftSubsystem.moveToInitial();
    }

    public void disabledInit() {}
}