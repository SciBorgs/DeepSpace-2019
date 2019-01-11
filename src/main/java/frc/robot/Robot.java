/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.LimelightSubsystem;
import frc.robot.subsystems.AutoSubsystem;

public class Robot extends IterativeRobot {
//    private static final String kDefaultAuto = "Default";
//    private static final String kCustomAuto = "My Auto";
    private String m_autoSelected;
    private final SendableChooser<String> m_chooser = new SendableChooser<>();
    public static LimelightSubsystem limelight = new LimelightSubsystem();
    public static AutoSubsystem autoSubsystem = new AutoSubsystem();
    public static PigeonIMU pigeon;
    public static DriveSubsystem driveSubsystem;
	public static TalonSRX lf, lm, lb, rf, rm, rb, pigeonTalon;

    @Override
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

        pigeon = new PigeonIMU(driveSubsystem.talonWithPigeon);
        pigeon.setYaw(0., 0);

        driveSubsystem = new DriveSubsystem();
    }

    @Override
    public void robotPeriodic() {
    }

    @Override
    public void autonomousInit() {
        pigeon.setYaw(0., 0);
        
        m_autoSelected = m_chooser.getSelected();
        System.out.println("Auto selected: " + m_autoSelected);
    }

    @Override
    public void autonomousPeriodic() {
    }

    @Override
    public void teleopPeriodic() {
    }

    @Override
    public void testPeriodic() {
    }

    @Override
    public void disabledInit() {
        pigeon.setYaw(0., 0);
    }
}
