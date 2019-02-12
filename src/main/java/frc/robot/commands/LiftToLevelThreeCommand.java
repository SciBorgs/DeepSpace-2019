package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class LiftToLevelThreeCommand extends Command {

    public LiftToLevelThreeCommand() {
        requires(Robot.liftSubsystem);
    }

    @Override protected void    initialize()  {Robot.liftSubsystem.liftPID.reset();}
    @Override protected void    execute()     {Robot.liftSubsystem.goToPositionThree();}
    @Override protected boolean isFinished()  {return true;}
    @Override protected void    end()         {return;}
    @Override protected void    interrupted() {end();}
}
