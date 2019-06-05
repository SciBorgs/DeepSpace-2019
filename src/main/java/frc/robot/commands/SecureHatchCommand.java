package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.logging.Logger.CommandStatus;

public class SecureHatchCommand extends InstantCommand {
    private final String FILENAME = "SecureHatchCommand.java";

    public SecureHatchCommand(){}

    @Override protected void execute() {
        Robot.logger.logCommandStatus(FILENAME, CommandStatus.Executing);
        Robot.driveSubsystem.defaultDriveMultilpier();
        Robot.intakeSubsystem.secureHatch();
    }
}