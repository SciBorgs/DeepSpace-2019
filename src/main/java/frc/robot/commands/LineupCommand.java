package frc.robot.commands;

import frc.robot.Robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;


public class LineupCommand extends Command {
    
    public LineupCommand(){}

    @Override protected void initialize(){
        //Robot.lineup.simpleResetInfo();
        //Robot.lineup.move();
    }
    @Override protected void execute(){
        return;
    }
    @Override protected boolean isFinished(){
        return true;
    }
    @Override protected void end(){
        return;
    }
    @Override protected void interrupted(){
        end();
    }
}
