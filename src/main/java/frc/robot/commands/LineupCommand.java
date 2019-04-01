package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.logging.Logger.DefaultValue;

public class LineupCommand extends InstantCommand {

    private final String fileName = "LineupCommand.java";
    
    public LineupCommand(){}

    @Override protected void execute(){
        Robot.logger.addData(this.fileName, Robot.logger.commandStatus, "executing", DefaultValue.Empty);
        Robot.lineup.simpleResetInfo();
        Robot.lineup.move();
    }
}
