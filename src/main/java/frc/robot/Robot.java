package frc.robot;
import frc.robot.subsystems.ArmSubsystem;
import frc.robot.subsystems.AutoSubsystem;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.LimelightSubsystem;
import frc.robot.subsystems.LineupSubsystem;
import frc.robot.subsystems.PositioningSubsystem;
import frc.robot.subsystems.RetroreflectiveTapeSubsystem;

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
    public static LimelightSubsystem limelight = new LimelightSubsystem();
    public static AutoSubsystem autoSubsystem = new AutoSubsystem();
    public static RetroreflectiveTapeSubsystem retroreflective = new RetroreflectiveTapeSubsystem();
    public static LineupSubsystem lineup = new LineupSubsystem();
    public static DriveSubsystem driveSubsystem = new DriveSubsystem();
    public static PigeonIMU pigeon;
	public static TalonSRX lf, lm, lb, rf, rm, rb, pigeonTalon;
	public static PositioningSubsystem pos;
	
    public static final double ARM_P_CONSTANT = .1;
    public static final double ARM_D_CONSTANT = .1;
    public static ArmSubsystem armSubsystem;
    

    public static OI oi;

    public void robotInit() {
        //m_chooser.setDefaultOption("Default Auto", kDefaultAuto); // These lines were causing errors and weren't necessary. They should either be deleted or restored at some point soon
        //m_chooser.addOption("My Auto", kCustomAuto);
        //SmartDashboard.putData("Auto choices", m_chooser);

		lf = new TalonSRX(PortMap.LEFT_FRONT_TALON);
		lm = new TalonSRX(PortMap.LEFT_MIDDLE_TALON);
		lb = new TalonSRX(PortMap.LEFT_BACK_TALON);
		rf = new TalonSRX(PortMap.RIGHT_FRONT_TALON);
		rm = new TalonSRX(PortMap.RIGHT_MIDDLE_TALON);
		rb = new TalonSRX(PortMap.RIGHT_BACK_TALON);
		pigeonTalon = new TalonSRX(PortMap.PIGEON_TALON);
        pigeon = new PigeonIMU(pigeonTalon);
        pigeon.setYaw(0., 0);
        driveSubsystem = new DriveSubsystem();
        //armSubsystem = new ArmSubsystem(/* Pass motor channel here */2);
        oi = new OI();
        pos = new PositioningSubsystem();

        System.out.println("roboinited");
        //new JoystickArmCommand(oi.leftStick.getTwist());
    }

    public void robotPeriodic() {
    	
    }

    public static double getPigeonAngle(){
		double[] yawPitchRoll = new double[3];
		pigeon.getYawPitchRoll(yawPitchRoll);
		//System.out.println("yaw: " + yawPitchRoll[0] + " pitch: " + yawPitchRoll[1] + " roll: " + yawPitchRoll[2]);
		return Math.toRadians(yawPitchRoll[0] % 360.); //raw goes from 0 to 22 and we want from 0 to 360
	}
    
    //TODO: make robot work lol
    
    public void autonomousInit() {
    	System.out.println("Yikes!");
        System.out.println("Auto selected: " + m_autoSelected);
    	pos.updatePositionTank();
        //lineup.resetInfo(.305 * 8, .305 * -2.2, Math.toRadians(-26));
        //System.out.println("INITIAL ANGLE: " + pos.getAngle());
        m_autoSelected = m_chooser.getSelected();
        pos.resetPosition();
        pigeon.enterCalibrationMode(CalibrationMode.Temperature, 10);
    }

    public void autonomousPeriodic() {
    	
    	System.out.println("[ ANGLE ]: " + pigeon.getFusedHeading() % 360.);
    	pos.updatePositionTank();
    	//lineup.move();
    	
    }
    
    @Override
    public void teleopInit() {
    	System.out.println("[     teleinitting    ]");
    	RobotCentricDriveCommand com = new RobotCentricDriveCommand();
    	com.start();
    }

    public void teleopPeriodic() {
    	Scheduler.getInstance().run();
    	pos.updatePositionTank();
    	//oi.switchCentricDriving.whenPressed(new ConditionalDriveCommand(new FieldCentricDriveCommand(), new RobotCentricDriveCommand()));
    }

    public void testPeriodic() {
    }

    public void disabledInit() {
    }
}