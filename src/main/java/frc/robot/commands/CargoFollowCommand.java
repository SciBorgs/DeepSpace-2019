package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class CargoFollowCommand extends Command {

    public CargoFollowCommand() {
        requires(Robot.driveSubsystem);
    }

    @Override protected void    initialize()  {return;}
    @Override protected void    execute()     {Robot.cargoFollowing.followBall();}
    @Override protected boolean isFinished()  {return true;}
    @Override protected void    end()         {return;}
    @Override protected void    interrupted() {end();}
}
