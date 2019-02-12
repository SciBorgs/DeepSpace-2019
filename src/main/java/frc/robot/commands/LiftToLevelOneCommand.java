package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class LiftToLevelOneCommand extends Command {

    public LiftToLevelOneCommand() {
        requires(Robot.liftSubsystem);
    }

    @Override protected void    initialize()  {Robot.liftSubsystem.liftPID.reset();}
    @Override protected void    execute()     {Robot.liftSubsystem.goToPositionOne();}
    @Override protected boolean isFinished()  {return true;}
    @Override protected void    end()         {return;}
    @Override protected void    interrupted() {end();}
}
