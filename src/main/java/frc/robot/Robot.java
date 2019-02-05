package frc.robot;
import frc.robot.subsystems.*;
import frc.robot.commands.*;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.ctre.phoenix.sensors.PigeonIMU.CalibrationMode;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;

import java.util.*;

public class Robot extends IterativeRobot {
//    private static final String kDefaultAuto = "Default";
//    private static final String kCustomAuto = "My Auto";
    private String m_autoSelected;
    private final SendableChooser<String> m_chooser = new SendableChooser<>();
    public static LimelightSubsystem limelightSubsystem = new LimelightSubsystem();
    public static RetroreflectiveTapeSubsystem retroreflectiveSubsystem = new RetroreflectiveTapeSubsystem();
    public static LineupSubsystem lineupSubsystem = new LineupSubsystem();
    public static DriveSubsystem driveSubsystem = new DriveSubsystem();
	public static PositioningSubsystem positioningSubsystem;// = new PositioningSubsystem();
    public static CargoFollowSubsystem cargoFollowSubsystem = new CargoFollowSubsystem();
    public static GearShiftSubsystem gearShiftSubsystem = new GearShiftSubsystem();
    public static ZLiftSubsystem zLiftSubsystem = new ZLiftSubsystem();
    public static PigeonIMU pigeon;
    public static CANSparkMax lf, lm, lb, rf, rm, rb;
    public static TalonSRX pigeonTalon;
    public static DigitalInput ballLimitSwitch, hatchLimitSwitch;
	
    public static final double ARM_P_CONSTANT = .1;
    public static final double ARM_D_CONSTANT = .1;    

    public static OI oi;

    public void robotInit() {


        new SwitchToCargoCommand().start();

        MotorType motorType = MotorType.kBrushed;
        MotorType motorType2 = MotorType.kBrushless;
		lf = new CANSparkMax(PortMap.LEFT_FRONT_SPARK,motorType2);
		lm = new CANSparkMax(PortMap.LEFT_MIDDLE_SPARK,motorType2);
        lb = new CANSparkMax(PortMap.LEFT_BACK_SPARK,motorType2);
        
		rf = new CANSparkMax(PortMap.RIGHT_FRONT_SPARK,motorType);
		rm = new CANSparkMax(PortMap.RIGHT_MIDDLE_SPARK,motorType);
		rb = new CANSparkMax(PortMap.RIGHT_BACK_SPARK,motorType);
		pigeonTalon = new TalonSRX(PortMap.PIGEON_TALON);
        pigeon = new PigeonIMU(pigeonTalon);
        ballLimitSwitch = new DigitalInput(PortMap.BALL_LIMIT_SWITCH);
        hatchLimitSwitch = new DigitalInput(PortMap.HATCH_LIMIT_SWITCH);

        pigeon.setYaw(0., 0);
        pigeon.enterCalibrationMode(CalibrationMode.Temperature, 10);
        oi = new OI();
        System.out.println("roboinited");
        //positioningSubsystem.updatePositionTank();
        Compressor c = new Compressor();
//        c.stop();
                
        try {
            System.out.println("LIDAR status: starting");
            boolean started = LidarServer.getInstance().start();
            System.out.println("LIDAR status" + (started ? "started" : "failed to start"));
        } catch (Throwable t) {
            System.out.println("LIDAR status: crashed -" + t);
            t.printStackTrace();
            throw t;
        }

    }

    public void robotPeriodic() {
        //positioningSubsystem.updatePositionTank();
        retroreflectiveSubsystem.modeToRetroreflectiveByLimitSwitch(); 
        gearShiftSubsystem.shiftGear(); 	
    }

    public static double getPigeonAngle(){
		double[] yawPitchRoll = new double[3];
		pigeon.getYawPitchRoll(yawPitchRoll);
		return Math.toRadians(yawPitchRoll[0] % 360.); //raw goes from 0 to 22 and we want from 0 to 360
	}
    
    //TODO: make robot work lol
    
    public void autonomousInit() {
        System.out.println("Auto selected: " + m_autoSelected);
        //positioningSubsystem.resetPosition();
        m_autoSelected = m_chooser.getSelected();
        gearShiftSubsystem.shiftUp();
    }

    public void autonomousPeriodic() {
        Hashtable<Double,Double> data = LidarServer.getInstance().lidarScan;

        System.out.println("size: " + data.size());
        if (data.size() > 0){
            System.out.print("[");
            for (double angle = 0; angle < 360; angle++){
                if (data.containsKey(angle))
                    System.out.print("(" + angle + "," + data.get(angle) + "), ");
            }
            System.out.println("]");
        }
    }
    
    @Override
    public void teleopInit() {
        new RobotCentricDriveCommand().start();
        gearShiftSubsystem.shiftDown();
    }

    public void teleopPeriodic() {
        Scheduler.getInstance().run();
    }

    public void testPeriodic() {
    }

    public void disabledInit() {
        zLiftSubsystem.reset();
    }
}