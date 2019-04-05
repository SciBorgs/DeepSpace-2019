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

public class Robot extends TimedRobot {
    private String m_autoSelected;
    public static IntakeSubsystem intakeSubsystem; // = new IntakeSubsystem();
    public static DriveSubsystem driveSubsystem; // = new DriveSubsystem();
	public static PositioningSubsystem positioningSubsystem; // = new PositioningSubsystem();
    public static LiftSubsystem liftSubsystem; // = new LiftSubsystem();
    public static GearShiftSubsystem gearShiftSubsystem; // = new GearShiftSubsystem();
    public static OI oi; // = new OI();
    public static Logger logger = new Logger();
    private final SendableChooser<String> m_chooser = new SendableChooser<>();
    public static LimelightSubsystem limelightSubsystem; // = new LimelightSubsystem();
    public static CargoFollowing cargoFollowing; // = new CargoFollowing();
    public static PneumaticsSubsystem pneumaticsSubsystem; // = new PneumaticsSubsystem();
    public static Lineup lineup; // = new Lineup();
    private final ControlScheme xboxControl = new XboxControl();
    private final PowerDistributionPanel pdp = new PowerDistributionPanel();
    private final DigitalOutput targetingLight = new DigitalOutput(5);
    private boolean lightOn = false;
    private boolean prevLightButton = false;
    private int attemptsSinceLastLog;
    public static final int LOG_PERIOD = 5;

    private void allPeriodicLogs(){
        driveSubsystem.periodicLog();
        gearShiftSubsystem.periodicLog();
        intakeSubsystem.periodicLog();
        limelightSubsystem.periodicLog();
        pneumaticsSubsystem.periodicLog();
        positioningSubsystem.periodicLog();
        liftSubsystem.periodicLog();
        lineup.periodicLog();
        cargoFollowing.periodicLog();
    }

    private CANSparkMax[] getSparks() {
        List<CANSparkMax> list = new ArrayList<>(); // this is inefficient but the code should be temporary
        Collections.addAll(list, driveSubsystem.getSparks());
        Collections.addAll(list, liftSubsystem.getSparks());
        return list.toArray(new CANSparkMax[0]);
    }

    private TalonSRX[] getTalons() {
        List<TalonSRX> list = new ArrayList<>();
        Collections.addAll(list, positioningSubsystem.getTalons());
        Collections.addAll(list, liftSubsystem.getTalons());
        Collections.addAll(list, intakeSubsystem.getTalons());
        return list.toArray(new TalonSRX[0]);
    }

    public void robotInit() {
        xboxControl.getShuffleboardCommand(pdp, liftSubsystem, pneumaticsSubsystem, getSparks(), getTalons(), liftSubsystem.getLiftSpark(), cargoFollowing.getPid(), driveSubsystem.getTankAnglePID(), driveSubsystem.getMaxOmegaGoal(), liftSubsystem.getArmPID(), liftSubsystem.getLiftPID(), lineup.getShiftPID()).start();
        attemptsSinceLastLog = 0;
        // positioningSubsystem.getPigeon().getPigeon().setYaw(0., 5);
        System.out.println("roboinited");
        // positioningSubsystem.updatePositionTank();


        // cargoFollowing.modeToCargo();

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
        //positioningSubsystem.updatePositionTank();
        //System.out.println("arm angle: " + Math.toDegrees(liftSubsystem.getArmAngle()));
        //System.out.println("unadjusted arm angle: " + Math.toDegrees(liftSubsystem.getUnadjustedArmAngle()));
        //positioningSubsystem.printPosition();
        //System.out.println("pigeon raw: " + positioningSubsystem.getPigeon().getAngle());
        //retroreflectiveSubsystem.modeToRetroreflectiveByLimitSwitch(); 
        //gearShiftSubsystem.shiftGear(); 	
        /*intakeSubsystem.secureCargo();
        if (intakeSubsystem.holdingGamePiece()){
            RetroreflectiveDetection.modeToRetroreflective();
        } else {
            cargoFollowing.modeToCargo();
        }*/
    }
        
    public void autonomousInit() {
        cargoFollowing.modeToCargo();
        new TankDriveCommand().start();
        //(new LiftCommand()).start();
        // new TankDriveCommand().start();
        pneumaticsSubsystem.startCompressor();
    }

    public void autonomousPeriodic() {
        //SmartDashboard.putNumber("Pressure Sensor PSI", pneumaticsSubsystem.getPressure());
        
        if(Robot.oi.leftStick.getPOV() == 0){
            Robot.liftSubsystem.setLiftSpeed(.4);
        }else if(Robot.oi.leftStick.getPOV() == 180){
            Robot.liftSubsystem.setLiftSpeed(-.4);
        }else{
            Robot.liftSubsystem.setLiftSpeed(0);
        }
        if(Robot.oi.rightStick.getPOV() == 0){
            Robot.liftSubsystem.setArmTiltSpeed(.55);
        }else if(Robot.oi.rightStick.getPOV() == 180){
            Robot.liftSubsystem.setArmTiltSpeed(-.55);
        }else if(liftSubsystem.manualArmMode){
            Robot.liftSubsystem.setArmTiltSpeed(0);
        }
        allPeriodicLogs();
        logDataPeriodic();
    }
    
    @Override
    public void teleopInit() {
        new TankDriveCommand().start();
    }

    public void teleopPeriodic() {
        //SmartDashboard.putNumber("Pressure Sensor PSI", pneumaticsSubsystem.getPressure());
        
        if(Robot.oi.leftStick.getPOV() == 0){
            Robot.liftSubsystem.setLiftSpeed(.3);
        }else if(Robot.oi.leftStick.getPOV() == 180){
            Robot.liftSubsystem.setLiftSpeed(-.3);
        }else{
            Robot.liftSubsystem.setLiftSpeed(0);
        }
        if(Robot.oi.rightStick.getPOV() == 0){
            Robot.liftSubsystem.setArmTiltSpeed(.55);
        }else if(Robot.oi.rightStick.getPOV() == 180){
            Robot.liftSubsystem.setArmTiltSpeed(-.55);
        }else if(liftSubsystem.manualArmMode){
            Robot.liftSubsystem.setArmTiltSpeed(0);
        }

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

        allPeriodicLogs();
        logDataPeriodic();
    }

    public void testPeriodic() {
        //liftSubsystem.moveToTarget(Target.Initial);
        //allPeriodicLogs();
        logDataPeriodic();
    }

    public void disabledInit() {
    }
}
