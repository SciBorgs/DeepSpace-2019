/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class CargoFollowCommand extends Command {

    public CargoFollowCommand(JoystickButton button) {
        requires(Robot.cargoFollowSubsystem);
    }

    @Override protected void    initialize()  {return;}
    @Override protected void    execute()     {Robot.cargoFollowSubsystem.followBall();}
    @Override protected boolean isFinished()  {return true;}
    @Override protected void    end()         {return;}
    @Override protected void    interrupted() {end();}
}
