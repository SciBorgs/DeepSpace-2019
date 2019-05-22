package frc.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import frc.robot.controlscheme.ControlScheme;
import frc.robot.controlscheme.XboxControl;
import frc.robot.subsystems.*;
import frc.robot.subsystems.LiftSubsystem.Target;
import frc.robot.commands.*;
import frc.robot.helpers.*;
import frc.robot.logging.*;
import frc.robot.logging.Logger.DefaultValue;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;

import java.util.*;

import com.ctre.phoenix.motorcontrol.ControlMode;

// FILE HAS NOT BEEN CLEANED UP //
public class Robot extends TimedRobot {
    private String m_autoSelected;
    public static Logger logger = new Logger();
    public static IntakeSubsystem intakeSubsystem = new IntakeSubsystem();
    public static DriveSubsystem driveSubsystem = new DriveSubsystem();
    public static EncoderSubsystem encoderSubsystem = new EncoderSubsystem();
    public static RobotPosition robotPosition = new RobotPosition();
    public static LiftSubsystem liftSubsystem = new LiftSubsystem();
    public static GearShiftSubsystem gearShiftSubsystem = new GearShiftSubsystem();
    private final SendableChooser<String> m_chooser = new SendableChooser<>();
    public static LimelightSubsystem limelightSubsystem = new LimelightSubsystem();
    public static Following following = new Following();
    public static PneumaticsSubsystem pneumaticsSubsystem = new PneumaticsSubsystem();
    public static Lineup lineup = new Lineup();
    private final ControlScheme xboxControl = new XboxControl();
    private final PowerDistributionPanel pdp = new PowerDistributionPanel();
    private final DigitalOutput targetingLight = new DigitalOutput(5);
    public static OI oi = new OI();
    private boolean lightOn = false;
    private boolean prevLightButton = false;
    private int attemptsSinceLastLog;
    public static double MANUAL_CASCADE_INPUT = 1;
    public static double MANUAL_CARRIAGE_INPUT = .55;
    public static final int LOG_PERIOD = 5;

    private void allPeriodicLogs(){
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
        List<CANSparkMax> list = new ArrayList<>(); // this is inefficient but the code should be temporary
        Collections.addAll(list, driveSubsystem.getSparks());
        Collections.addAll(list, liftSubsystem.getSparks());
        return list.toArray(new CANSparkMax[0]);
    }

    private TalonSRX[] getTalons() {
        List<TalonSRX> list = new ArrayList<>();
        Collections.addAll(list, robotPosition.getTalons());
        Collections.addAll(list, liftSubsystem.getTalons());
        Collections.addAll(list, intakeSubsystem.getTalons());
        return list.toArray(new TalonSRX[0]);
    }

    public void robotInit() {
        xboxControl.getShuffleboardCommand(pdp, liftSubsystem, pneumaticsSubsystem, getSparks(), getTalons(), liftSubsystem.getLiftSpark(), following.getPid(), driveSubsystem.getTankAnglePID(), driveSubsystem.getMaxOmegaGoal(), liftSubsystem.getArmPID(), liftSubsystem.getLiftPID(), lineup.getShiftPID()).start();
        attemptsSinceLastLog = 0;
        // positioningSubsystem.getPigeon().getPigeon().setYaw(0., 5);
        System.out.println("roboinited");
        robotPosition.updatePositionTank();

        //pneumaticsSubsystem.stopCompressor();

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

    public void logDataPeriodic(){
        if (LOG_PERIOD == attemptsSinceLastLog){
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
        //(new LiftCommand()).start();
        pneumaticsSubsystem.startCompressor();
    }

    public void autonomousPeriodic() {
        //SmartDashboard.putNumber("Pressure Sensor PSI", pneumaticsSubsystem.getPressure());
        new SwerveTankDriveCommand().start();
        manualArmAndCascade();
        //allPeriodicLogs();
        //logDataPeriodic();
    }
    
    @Override
    public void teleopInit() {
        liftSubsystem.setLiftSpeed(0);
        (new LiftCommand()).start();
    }

    public void manualArmAndCascade(){
        if(Robot.oi.leftStick.getPOV() == 0){
            //System.out.println("up");
            Robot.liftSubsystem.setLiftSpeed(MANUAL_CASCADE_INPUT);
        }else if(Robot.oi.leftStick.getPOV() == 180){
            //System.out.println("down");
            Robot.liftSubsystem.setLiftSpeed(-MANUAL_CASCADE_INPUT);
        //}else{
        }else if(liftSubsystem.manualCascadeMode){
            //System.out.println("stopping");
            Robot.liftSubsystem.setLiftSpeed(0);
        }
    }

    public void teleopPeriodic() {
        //SmartDashboard.putNumber("Pressure Sensor PSI", pneumaticsSubsystem.getPressure());
        manualArmAndCascade();
        new TankDriveCommand().start();

        boolean targetLightButton = oi.xboxController.getBButton();

        if(!lightOn){
            if(targetLightButton && !prevLightButton){
                targetingLight.set(true);
                lightOn = true;
            }
        }else{
            if(targetLightButton && !prevLightButton){
                targetingLight.set(false);
                lightOn = false;
            }
        }
        prevLightButton = targetLightButton;

        pneumaticsSubsystem.startCompressor();
        //allPeriodicLogs();
        //logDataPeriodic();
    }

    public void testPeriodic() {
        //liftSubsystem.moveToTarget(Target.Initial);
        liftSubsystem.moveToInitial();
        following.modeToCargo();
        //liftSubsystem.moveLiftToHeight(Utils.inchesToMeters(40));
        System.out.println("leftPOV: " + Robot.oi.leftStick.getPOV());
        //liftSubsystem.currentlAtInitial();
        //allPeriodicLogs();
        //logDataPeriodic();
    }

    public void disabledInit() {
    }
}
