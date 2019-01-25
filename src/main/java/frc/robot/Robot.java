package org.usfirst.frc.team1155.robot;
import org.usfirst.frc.team1155.robot.subsystems.ArmSubsystem;
import org.usfirst.frc.team1155.robot.subsystems.AutoSubsystem;
import org.usfirst.frc.team1155.robot.subsystems.DriveSubsystem;
import org.usfirst.frc.team1155.robot.subsystems.LimelightSubsystem;
import org.usfirst.frc.team1155.robot.subsystems.LineupSubsystem;
import org.usfirst.frc.team1155.robot.subsystems.PositioningSubsystem;
import org.usfirst.frc.team1155.robot.subsystems.RetroreflectiveTapeSubsystem;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;

import edu.wpi.first.wpilibj.IterativeRobot;
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


        //new JoystickArmCommand(oi.leftStick.getTwist());
    }

    public void robotPeriodic() {
    }

    public static double getPigeonAngle(){
		double[] yawPitchRoll = new double[3];
		pigeon.getYawPitchRoll(yawPitchRoll);
		//System.out.println("yaw: " + yawPitchRoll[0] + " pitch: " + yawPitchRoll[1] + " roll: " + yawPitchRoll[2]);
		return Math.toRadians(yawPitchRoll[0] % 360.);
	}
    
    //TODO: make robot work lol
    
    public void autonomousInit() {
        System.out.println("Auto selected: " + m_autoSelected);
        pigeon.setYaw(0, 30);
    	pos.updatePositionTank();
        lineup.resetInfo(.305 * 6.3, .305 * -.6, Math.toRadians(-19));
        m_autoSelected = m_chooser.getSelected();
        //pos.resetPosition();
    }

    public void autonomousPeriodic() {
    	pos.updatePositionTank();
    }

    public void teleopPeriodic() {
    	//oi.switchCentricDriving.whenPressed(new ConditionalDriveCommand(new FieldCentricDriveCommand(), new RobotCentricDriveCommand()));
    }

    public void testPeriodic() {
    }

    public void disabledInit() {
    }
}