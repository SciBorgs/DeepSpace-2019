package frc.robot.commands;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.Robot;
import frc.robot.logging.Logger.CommandStatus;
import frc.robot.logging.Logger.DefaultValue;

public class ClimbToggleFrontCommand extends InstantCommand {
    private final String fileName = "ClimbToggleFrontCommand.java";

    public ClimbToggleFrontCommand(){
        requires(Robot.climbSubsystem);
    }

    @Override protected void execute() {
        Robot.logger.logCommandStatus(this.fileName, CommandStatus.Executing);
        System.out.println("front climb extend");
        Robot.climbSubsystem.toggleFront();
    }
}