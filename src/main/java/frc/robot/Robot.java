package frc.robot;
import frc.robot.subsystems.*;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.ctre.phoenix.sensors.PigeonIMU.CalibrationMode;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

public class Robot extends IterativeRobot {
//    private static final String kDefaultAuto = "Default";
//    private static final String kCustomAuto = "My Auto";
    private String m_autoSelected;
    private final SendableChooser<String> m_chooser = new SendableChooser<>();
    public static LimelightSubsystem limelightSubsystem = new LimelightSubsystem();
    public static AutoSubsystem autoSubsystem = new AutoSubsystem();
    public static RetroreflectiveTapeSubsystem retroreflectiveSubsystem = new RetroreflectiveTapeSubsystem();
    public static LineupSubsystem lineupSubsystem = new LineupSubsystem();
    public static DriveSubsystem driveSubsystem = new DriveSubsystem();
	public static PositioningSubsystem positioningSubsystem = new PositioningSubsystem();
    public static CargoFollowSubsystem cargoFollowSubsystem = new CargoFollowSubsystem();
    public static PigeonIMU pigeon;
	public static TalonSRX lf, lm, lb, rf, rm, rb, pigeonTalon;
	
    public static final double ARM_P_CONSTANT = .1;
    public static final double ARM_D_CONSTANT = .1;    

    public static OI oi;

    public void robotInit() {

		lf = new TalonSRX(PortMap.LEFT_FRONT_TALON);
		lm = new TalonSRX(PortMap.LEFT_MIDDLE_TALON);
		lb = new TalonSRX(PortMap.LEFT_BACK_TALON);
		rf = new TalonSRX(PortMap.RIGHT_FRONT_TALON);
		rm = new TalonSRX(PortMap.RIGHT_MIDDLE_TALON);
		rb = new TalonSRX(PortMap.RIGHT_BACK_TALON);
		pigeonTalon = new TalonSRX(PortMap.PIGEON_TALON);
        pigeon = new PigeonIMU(pigeonTalon);
        pigeon.setYaw(0., 0);
        pigeon.enterCalibrationMode(CalibrationMode.Temperature, 10);
        oi = new OI();
        System.out.println("roboinited");
        pos.updatePositionTank();
    }

    public void robotPeriodic() {
    	positioningSubsystem.updatePositionTank();  	
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