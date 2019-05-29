package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.logging.Logger.CommandStatus;

public class ToggleArmCommand extends InstantCommand {
    private final String FILENAME = "ToggleArmCommand.java";

    public ToggleArmCommand() {}

    @Override protected void execute() {
		Robot.logger.logCommandStatus(FILENAME, CommandStatus.Executing);
        Robot.intakeSubsystem.toggleArm();
    }
}