package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.logging.Logger.CommandStatus;

public class ReleaseHatchCommand extends InstantCommand {
    private final String fileName = "ReleaseHatchCommand.java";
    private final double RELEASE_HATCH_DRIVE_MULTIPLIER = 0.5;

    @Override protected void execute(){
		Robot.logger.logCommandStatus(fileName, CommandStatus.Executing);
        Robot.intakeSubsystem.releaseHatch();
        Robot.intakeSubsystem.closeArm();
        Robot.driveSubsystem.setDriveMultiplier(RELEASE_HATCH_DRIVE_MULTIPLIER);           

    }
}
