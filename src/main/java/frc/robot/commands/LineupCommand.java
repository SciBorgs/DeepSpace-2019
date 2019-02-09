package frc.robot.commands;

import frc.robot.Robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;


public class LineupCommand extends Command {
    
    public LineupCommand() {
    }

    @Override protected void    initialize()  {Robot.lineupSubsystem.resetFound();}
    @Override protected void    execute()     {
        //Robot.lineupSubsystem.autoResetInfo();
        Robot.lineupSubsystem.simpleResetInfo();
        Robot.lineupSubsystem.move();
    }
    @Override protected boolean isFinished()  {return Robot.lineupSubsystem.getShiftPID().targetReached() &&
                                                      Robot.lineupSubsystem.getForwardPID().targetReached();}
    @Override protected void    end()         {Robot.driveSubsystem.setSpeedTank(0, 0);}
    @Override protected void    interrupted() {end();}
}
