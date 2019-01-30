/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team1155.robot.commands;

import org.usfirst.frc.team1155.robot.Robot;
import org.usfirst.frc.team1155.robot.subsystems.DriveSubsystem;
import org.usfirst.frc.team1155.robot.subsystems.DriveSubsystem.Modes;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;


public class RobotCentricDriveCommand extends Command {
    private Joystick rightStick, leftStick;
    
    public RobotCentricDriveCommand() {
        requires(Robot.driveSubsystem);

        rightStick = Robot.oi.rightStick;
        leftStick = Robot.oi.leftStick;
        System.out.println("CONSTRUCTED");
    }

    @Override
    protected void initialize() {
    	System.out.println("INITIALIZING");
        Robot.driveSubsystem.setSpeedTank(0, 0);
    }

    @Override
    protected void execute() {
    	System.out.println("[     executing...     ]");
        Robot.driveSubsystem.setSpeed(leftStick, rightStick);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

    @Override
    protected void end() {
        Robot.driveSubsystem.setSpeedTank(0, 0);
    }

    @Override
    protected void interrupted() {
        end();
    }
}
