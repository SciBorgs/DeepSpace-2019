package frc.robot.commands;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.Robot;
import frc.robot.logging.Logger.CommandStatus;
import frc.robot.logging.Logger.DefaultValue;

public class ClimbToggleBackCommand extends InstantCommand {
    private final String fileName = "ClimbToggleBackCommand.java";

    public ClimbToggleBackCommand(){
        requires(Robot.climbSubsystem);
    }

    @Override protected void execute() {
        Robot.logger.logCommandStatus(this.fileName, CommandStatus.Executing);
        Robot.climbSubsystem.toggleBack();
    }
}