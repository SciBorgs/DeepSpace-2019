package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.logging.Logger.DefaultValue;


public class SuckCommand extends InstantCommand {
	private final String fileName = "SuckCommand.java";

    public SuckCommand() {
        requires(Robot.intakeSubsystem);
    }

    @Override protected void execute() {
        Robot.logger.addData(this.fileName, Robot.logger.commandStatus, "executing", DefaultValue.Empty);
        System.out.println("suck");
        Robot.intakeSubsystem.openArm();
        Robot.intakeSubsystem.suck();
    }
}
