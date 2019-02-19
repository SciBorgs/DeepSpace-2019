package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;

public class LineupCommand extends InstantCommand {
    
    public LineupCommand(){}

    @Override protected void execute(){
        Robot.lineup.simpleResetInfo();
        Robot.lineup.move();
    }
}
