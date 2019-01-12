/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;
import java.util.Hashtable;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import frc.robot.commands.JoystickArmCommand;
import frc.robot.subsystems.ArmSubsystem;
import frc.robot.subsystems.AutoSubsystem;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.LimelightSubsystem;
import frc.robot.subsystems.RetroreflectiveTapeSubsystem;

public class Robot extends IterativeRobot {
//    private static final String kDefaultAuto = "Default";
//    private static final String kCustomAuto = "My Auto";
    private String m_autoSelected;
    private final SendableChooser<String> m_chooser = new SendableChooser<>();
    public static LimelightSubsystem limelight = new LimelightSubsystem();
    public static AutoSubsystem autoSubsystem = new AutoSubsystem();
    public static RetroreflectiveTapeSubsystem retroreflective = new RetroreflectiveTapeSubsystem();
    public static PigeonIMU pigeon;
    public static DriveSubsystem driveSubsystem;
	public static TalonSRX lf, lm, lb, rf, rm, rb, pigeonTalon;
	
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
		pigeonTalon = lf;

        driveSubsystem = new DriveSubsystem();
        armSubsystem = new ArmSubsystem(/* Pass motor channel here */2);
        oi = new OI();

        pigeon = new PigeonIMU(pigeonTalon);
        pigeon.setYaw(0., 0);

        new JoystickArmCommand(oi.leftStick.getTwist());
    }

    public void robotPeriodic() {
    }

    public static double getPigeonAngle(){
		double[] yawPitchRoll = new double[3];
		pigeon.getYawPitchRoll(yawPitchRoll);
		//System.out.println("PigoenAngle: " + yawPitchRoll[0] % 360.);
		return Math.toRadians(yawPitchRoll[0] % 360.);
	}
    
    public void autonomousInit() {
        pigeon.setYaw(0., 0);

        m_autoSelected = m_chooser.getSelected();
        System.out.println("Auto selected: " + m_autoSelected);
    }

    public void autonomousPeriodic() {
    	Hashtable<String,Double> data = retroreflective.extractData();
    }

    public void teleopPeriodic() {
    }

    public void testPeriodic() {
    }

    public void disabledInit() {
        pigeon.setYaw(0., 0);
    }
}
