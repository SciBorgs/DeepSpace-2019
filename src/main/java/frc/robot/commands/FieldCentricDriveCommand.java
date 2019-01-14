/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team1155.robot.commands;

import org.usfirst.frc.team1155.robot.Robot;
import org.usfirst.frc.team1155.robot.subsystems.DriveSubsystem.Modes;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;

public class FieldCentricDriveCommand extends Command {

    public FieldCentricDriveCommand() {
        requires(Robot.driveSubsystem);
        
    }

    @Override
    protected void initialize() {
        Robot.driveSubsystem.setSpeedMecanum(0, 0, 0);
    }

    @Override
    protected void execute() {
        Robot.driveSubsystem.setSpeed(Robot.oi.rightStick, Robot.oi.leftStick, Modes.FIELD);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

    @Override
    protected void end() {
        Robot.driveSubsystem.setSpeedMecanum(0, 0, 0);
    }

    @Override
    protected void interrupted() {
        end();
    }
}
