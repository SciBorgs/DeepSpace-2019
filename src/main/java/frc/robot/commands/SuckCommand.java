package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.logging.Logger.CommandStatus;


public class SuckCommand extends InstantCommand {
	private final static String FILE_NAME = "SuckCommand.java";

    public SuckCommand() {
        requires(Robot.intakeSubsystem);
    }

    @Override protected void execute() {
		Robot.logger.logCommandStatus(FILE_NAME, CommandStatus.Executing);
        Robot.intakeSubsystem.openArm();
        Robot.intakeSubsystem.suck();
    }
}
