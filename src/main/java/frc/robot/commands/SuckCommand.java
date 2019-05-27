package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.logging.Logger.CommandStatus;


public class SuckCommand extends InstantCommand {
	private final String fileName = "SuckCommand.java";

    public SuckCommand() {
        requires(Robot.intakeSubsystem);
    }

    @Override protected void execute() {
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Executing);
        Robot.intakeSubsystem.openArm();
        Robot.intakeSubsystem.suck();
    }
}
