package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;

public class ResetLineupInfoCommand extends InstantCommand {
    
    public ResetLineupInfoCommand() {}

    @Override protected void execute(){
        Robot.lineup.resetFound();
    }
}
