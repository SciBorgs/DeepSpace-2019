/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.OI;
import frc.robot.Robot;

public class RobotCentricDriveCommand extends Command {
    private Joystick rightStick, leftStick;
    
    public RobotCentricDriveCommand() {
        rightStick = OI.rightStick;
        leftStick = OI.leftStick;
    }

    @Override
    protected void initialize() {
        Robot.driveSubsystem.mecanumDrive(0, 0, 0);
    }

    @Override
    protected void execute() {
        Robot.driveSubsystem.setSpeed(rightStick, leftStick);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

    @Override
    protected void end() {
        Robot.driveSubsystem.mecanumDrive(0, 0, 0);
    }

    @Override
    protected void interrupted() {
        end();
    }
}