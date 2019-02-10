package frc.robot.commands;

import frc.robot.Robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;


public class LineupCommand extends Command {
    
    public LineupCommand() {
    }

    @Override protected void    initialize()  {Robot.lineup.resetFound();}
    @Override protected void    execute()     {
        //Robot.lineup.autoResetInfo();
        Robot.lineup.simpleResetInfo();
        Robot.lineup.move();
    }
    @Override protected boolean isFinished()  {return Robot.lineup.getShiftPID().targetReached() &&
                                                      Robot.lineup.getForwardPID().targetReached();}
    @Override protected void    end()         {Robot.driveSubsystem.setSpeedTank(0, 0);}
    @Override protected void    interrupted() {end();}
}
