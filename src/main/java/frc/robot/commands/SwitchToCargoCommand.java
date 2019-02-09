package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class SwitchToCargoCommand extends Command {

    public SwitchToCargoCommand() {}

    @Override protected void    initialize()  {Robot.cargoFollowing.modeToCargo();}
    @Override protected void    execute()     {return;}
    @Override protected boolean isFinished()  {return true;}
    @Override protected void    end()         {return;}
    @Override protected void    interrupted() {end();}
}
