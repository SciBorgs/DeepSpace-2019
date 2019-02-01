package frc.robot;
import frc.robot.subsystems.*;
import frc.robot.commands.RobotCentricDriveCommand;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANSparkMax;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.ctre.phoenix.sensors.PigeonIMU.CalibrationMode;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.DigitalInput;

public class Robot extends IterativeRobot {
//    private static final String kDefaultAuto = "Default";
//    private static final String kCustomAuto = "My Auto";
    private String m_autoSelected;
    private final SendableChooser<String> m_chooser = new SendableChooser<>();
    public static LimelightSubsystem limelightSubsystem = new LimelightSubsystem();
    public static RetroreflectiveTapeSubsystem retroreflectiveSubsystem = new RetroreflectiveTapeSubsystem();
    public static LineupSubsystem lineupSubsystem = new LineupSubsystem();
    public static DriveSubsystem driveSubsystem = new DriveSubsystem();
	public static PositioningSubsystem positioningSubsystem = new PositioningSubsystem();
    public static CargoFollowSubsystem cargoFollowSubsystem = new CargoFollowSubsystem();
    public static PigeonIMU pigeon;
    public static SparkMax lf, lm, lb, rf, rm, rb;
    public static Talon pigeonTalon;
    public static DigitalInput ballLimitSwitch, hatchLimitSwitch;
	
    public static final double ARM_P_CONSTANT = .1;
    public static final double ARM_D_CONSTANT = .1;    

    public static OI oi;

    public void robotInit() {
		lf = new CanSparkMax(PortMap.LEFT_FRONT_SPARK);
		lm = new CanSparkMax(PortMap.LEFT_MIDDLE_SPARK);
		lb = new CanSparkMax(PortMap.LEFT_BACK_SPARK);
		rf = new CanSparkMax(PortMap.RIGHT_FRONT_SPARK);
		rm = new CanSparkMax(PortMap.RIGHT_MIDDLE_SPARK);
		rb = new CanSparkMax(PortMap.RIGHT_BACK_SPARK);
		pigeonTalon = new CanSparkMax(PortMap.PIGEON_TALON);
        pigeon = new PigeonIMU(pigeonTalon);
        ballLimitSwitch = new DigitalInput(PortMap.BALL_LIMIT_SWITCH);
        hatchLimitSwitch = new DigitalInput(PortMap.HATCH_LIMIT_SWITCH);

        pigeon.setYaw(0., 0);
        pigeon.enterCalibrationMode(CalibrationMode.Temperature, 10);
        oi = new OI();
        System.out.println("roboinited");
        positioningSubsystem.updatePositionTank();
    }

    public void robotPeriodic() {
        positioningSubsystem.updatePositionTank();
        retroreflectiveSubsystem.modeToRetroreflectiveByLimitSwitch();  	
    }

    public static double getPigeonAngle(){
		double[] yawPitchRoll = new double[3];
		pigeon.getYawPitchRoll(yawPitchRoll);
		return Math.toRadians(yawPitchRoll[0] % 360.); //raw goes from 0 to 22 and we want from 0 to 360
	}
    
    //TODO: make robot work lol
    
    public void autonomousInit() {
        System.out.println("Auto selected: " + m_autoSelected);
        positioningSubsystem.resetPosition();
        m_autoSelected = m_chooser.getSelected();
    }

    public void autonomousPeriodic() {
    }
    
    @Override
    public void teleopInit() {
    	new RobotCentricDriveCommand().start();
    }

    public void teleopPeriodic() {
    	Scheduler.getInstance().run();
    }

    public void testPeriodic() {
    }

    public void disabledInit() {
    }
}