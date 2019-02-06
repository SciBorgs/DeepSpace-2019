/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import frc.robot.Robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;


public class LineupCommand extends Command {
    private double yChange, xChange, angleChange;
    
    public LineupCommand() {
        yChange = 1; //Temporary values
        xChange = 1;
        angleChange = 1;
    }

    @Override protected void    initialize()  {Robot.lineupSubsystem.resetInfo(yChange, xChange, angleChange);}
    @Override protected void    execute()     {Robot.lineupSubsystem.move();}
    @Override protected boolean isFinished()  {return Math.abs(Robot.lineupSubsystem.getShiftPID().getOutput()) < .001 && Math.abs(Robot.lineupSubsystem.getForwardPID().getOutput()) < .001;}
    @Override protected void    end()         {Robot.driveSubsystem.setSpeedTank(0, 0);}
    @Override protected void    interrupted() {end();}
}
