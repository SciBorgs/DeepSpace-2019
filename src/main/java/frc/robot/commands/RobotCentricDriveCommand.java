/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import frc.robot.Robot;
import frc.robot.subsystems.DriveSubsystem;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;


public class RobotCentricDriveCommand extends Command {
    private Joystick rightStick, leftStick;
    
    public RobotCentricDriveCommand() {
        rightStick = Robot.oi.rightStick;
        leftStick = Robot.oi.leftStick;
    }

    @Override protected void    initialize()  {Robot.driveSubsystem.setSpeedTank(0, 0);}
    @Override protected void    execute()     {Robot.driveSubsystem.setSpeedRaw(leftStick, rightStick);}
    @Override protected boolean isFinished()  {return false;}
    @Override protected void    end()         {Robot.driveSubsystem.setSpeedTank(0, 0);}
    @Override protected void    interrupted() {end();}
}
