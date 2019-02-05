package frc.robot;
import frc.robot.subsystems.*;
import frc.robot.commands.*;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANSparkMax;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.ctre.phoenix.sensors.PigeonIMU.CalibrationMode;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;

import java.util.*;

public class Robot extends IterativeRobot {
    private String m_autoSelected;
    private final SendableChooser<String> m_chooser = new SendableChooser<>();
    public static LimelightSubsystem limelightSubsystem;// = new LimelightSubsystem();
    public static RetroreflectiveTapeSubsystem retroreflectiveSubsystem;// = new RetroreflectiveTapeSubsystem();
    public static LineupSubsystem lineupSubsystem;// = new LineupSubsystem();
    public static DriveSubsystem driveSubsystem;// = new DriveSubsystem();
	public static PositioningSubsystem positioningSubsystem;// = new PositioningSubsystem();
    public static CargoFollowSubsystem cargoFollowSubsystem;// = new CargoFollowSubsystem();
    public static GearShiftSubsystem gearShiftSubsystem;// = new GearShiftSubsystem();
    public static ZLiftSubsystem zLiftSubsystem;// = new ZLiftSubsystem();
    public static LidarSubsystem lidarSubsystem = new LidarSubsystem();
    public static PigeonIMU pigeon;
    public static TalonSRX pigeonTalon;
    public static DigitalInput ballLimitSwitch, hatchLimitSwitch;
	
    public static final double ARM_P_CONSTANT = .1;
    public static final double ARM_D_CONSTANT = .1;    

    public static OI oi;

    public void robotInit() {


       // new SwitchToCargoCommand().start();

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
        //retroreflectiveSubsystem.modeToRetroreflectiveByLimitSwitch(); 
        //gearShiftSubsystem.shiftGear(); 	
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
        //gearShiftSubsystem.shiftUp();
    }

    public void autonomousPeriodic() {
        //System.out.println("rotation: " + Math.toDegrees(lidarSubsystem.wallRotation(350,10)));
        //System.out.println("is wall?: " + lidarSubsystem.isWall(lidarSubsystem.fetchScan(), 345, 15));
        Hashtable<String,Double> data = lidarSubsystem.hatchInfo(330, 30);
        System.out.print("hatch detected?: ");
        System.out.println(data.get("detected"));
        if (data.get("detected") == 1){
            System.out.print("angle: ");
            System.out.println(Math.toDegrees(data.get("angle")));
            System.out.print("parallel: ");
            System.out.println(data.get("parallel") * Utils.metersToInches);
            System.out.print("shift: ");
            System.out.println(data.get("shift") * Utils.metersToInches);
        }
    }
    
    @Override
    public void teleopInit() {
        //new RobotCentricDriveCommand().start();
        //gearShiftSubsystem.shiftDown();
    }

    public void teleopPeriodic() {
        Scheduler.getInstance().run();
    }

    public void testPeriodic() {
    }

    public void disabledInit() {
        //zLiftSubsystem.reset();
    }
}