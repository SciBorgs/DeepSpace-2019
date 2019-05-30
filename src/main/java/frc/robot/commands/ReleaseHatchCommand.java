package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.logging.Logger.CommandStatus;

public class ReleaseHatchCommand extends InstantCommand {
    private final String fileName = "ReleaseHatchCommand.java";
    private double RELEASE_HATCH_DRIVE_MULTIPLIER = 0.5;

    @Override protected void execute(){
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Executing);
        Robot.intakeSubsystem.releaseHatch();
        Robot.intakeSubsystem.closeArm();
        Robot.driveSubsystem.setDriveMultiplier(this.RELEASE_HATCH_DRIVE_MULTIPLIER);           

    }
}