package frc.robot;
import frc.robot.subsystems.*;
import frc.robot.commands.*;
import frc.robot.helpers.*;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
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
    public static LimelightSubsystem limelightSubsystem = new LimelightSubsystem();
    public static RetroreflectiveTapeSubsystem retroreflectiveSubsystem = new RetroreflectiveTapeSubsystem();
    public static LineupSubsystem lineupSubsystem = new LineupSubsystem();
    public static DriveSubsystem driveSubsystem = new DriveSubsystem();
	public static PositioningSubsystem positioningSubsystem = new PositioningSubsystem();
    public static CargoFollowing cargoFollowing = new CargoFollowing();
    public static GearShiftSubsystem gearShiftSubsystem = new GearShiftSubsystem();
    public static ZLiftSubsystem zLiftSubsystem = new ZLiftSubsystem();
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
        positioningSubsystem.updatePositionTank();
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
        positioningSubsystem.updatePositionTank();
        positioningSubsystem.printPosition();
        //retroreflectiveSubsystem.modeToRetroreflectiveByLimitSwitch(); 
        //gearShiftSubsystem.shiftGear(); 	
    }

    public static double getPigeonAngle(){
        return 0;
        //double[] yawPitchRoll = new double[3];
		//pigeon.getYawPitchRoll(yawPitchRoll);
		//return Math.toRadians(yawPitchRoll[0] % 360.); //raw goes from 0 to 22 and we want from 0 to 360
	}
    
    //TODO: make robot work lol
    
    public void autonomousInit() {
        System.out.println("Auto selected: " + m_autoSelected);
        //positioningSubsystem.resetPosition();
        m_autoSelected = m_chooser.getSelected();
        //gearShiftSubsystem.shiftUp();
    }

    public void autonomousPeriodic() {
    }
    
    @Override
    public void teleopInit() {
        new TankDriveCommand().start();
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