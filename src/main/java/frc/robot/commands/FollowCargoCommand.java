/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class FollowCargoCommand extends Command {

    public FollowCargoCommand() {
        requires(Robot.cargoFollowSubsystem);
    }

    @Override protected void    initialize()  {Robot.cargoFollow.ballLimelightMode();
    @Override protected void    execute()     {Robot.cargoFollow.followBall();}
    @Override protected boolean isFinished()  {return true;}
    @Override protected void    end()         {Robot.armSubsystem.setSpeed(0);}
    @Override protected void    interrupted() {end();}
}