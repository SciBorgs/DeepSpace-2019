/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.commands.JoystickArmCommand;
import frc.robot.subsystems.ArmSubsystem;
import frc.robot.subsystems.DriveSubsystem;
import main.java.frc.robot.subsystems.LimelightSubsystem;

public class Robot extends IterativeRobot {
    private static final String kDefaultAuto = "Default";
    private static final String kCustomAuto = "My Auto";
    private String m_autoSelected;
    private final SendableChooser<String> m_chooser = new SendableChooser<>();
    public static LimelightSubsystem limelight = new LimelightSubsystem();
    public static PigeonIMU pigeon;
    public static DriveSubsystem driveSubsystem;

    public static final double ARM_P_CONSTANT;
    public static final double ARM_D_CONSTANT;
    public static ArmSubsystem armSubsystem;

    public static OI oi;

    public void robotInit() {
        m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
        m_chooser.addOption("My Auto", kCustomAuto);
        SmartDashboard.putData("Auto choices", m_chooser);

        driveSubsystem = new DriveSubsystem();
        armSubsystem = new ArmSubsystem(/* Pass motor channel here */);
        oi = new OI();

        pigeon = new PigeonIMU(driveSubsystem.talonWithPigeon);
        pigeon.setYaw(0., 0);

        new JoystickArmCommand(oi.leftStick.getTwist());
    }

    public void robotPeriodic() {
    }

    public void autonomousInit() {
        pigeon.setYaw(0., 0);

        m_autoSelected = m_chooser.getSelected();
        System.out.println("Auto selected: " + m_autoSelected);
    }

    public void autonomousPeriodic() {
    }

    public void teleopPeriodic() {
    }

    public void testPeriodic() {
    }

    public void disabledInit() {
        pigeon.setYaw(0., 0);
    }
}
