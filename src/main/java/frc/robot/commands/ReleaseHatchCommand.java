package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.logging.Logger.CommandStatus;

public class ReleaseHatchCommand extends InstantCommand {
    private final String fileName = "ReleaseHatchCommand.java";
    private double ReleaseHatchDriveMultiplier = 0.5;

    @Override protected void execute(){
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Executing);
        Robot.intakeSubsystem.releaseHatch();
        Robot.intakeSubsystem.closeArm();
        Robot.driveSubsystem.setDriveMultiplier(this.ReleaseHatchDriveMultiplier);           

    }
}
