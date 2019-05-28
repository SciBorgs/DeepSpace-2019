package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.logging.Logger.DefaultValue;
import frc.robot.logging.Logger.CommandStatus;

public class SecureHatchCommand extends InstantCommand {
    private final String fileName = "SecureHatchCommand.java";

    public SecureHatchCommand() {}

    @Override protected void execute() {
        Robot.logger.logCommandStatus(this.fileName, CommandStatus.Executing);
        Robot.driveSubsystem.defaultDriveMultilpier();
        Robot.intakeSubsystem.secureHatch();
    }
}