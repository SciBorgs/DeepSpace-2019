package frc.robot.commands;

import frc.robot.Robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;


public class ResetLineupInfoCommand extends Command {
    
    public ResetLineupInfoCommand() {
    }

    @Override protected void initialize(){
        Robot.lineup.resetFound();
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
