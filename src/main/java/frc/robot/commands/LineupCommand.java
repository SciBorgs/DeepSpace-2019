package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.logging.Logger.CommandStatus;

public class LineupCommand extends Command {
    private static final String FILENAME = "LineupCommand.java";
    
    public LineupCommand() {}

    @Override protected void initialize() {
		Robot.logger.logCommandStatus(FILENAME, CommandStatus.Initializing);
		Robot.following.resetLineupPID();
    }

    @Override protected void execute() {
		Robot.logger.logCommandStatus(FILENAME, CommandStatus.Executing);
        Robot.following.lineup();
        Robot.driveSubsystem.assistedDriveMode();
    }

	@Override protected boolean isFinished() { return !Robot.oi.lineupButton.get(); }

	@Override protected void end() {
		Robot.logger.logCommandStatus(FILENAME, CommandStatus.Ending);
        Robot.driveSubsystem.manualDriveMode();
        Robot.following.modeToCargo();
	}

	@Override protected void interrupted() {
		Robot.logger.logCommandStatus(FILENAME, CommandStatus.Interrupted);
		end();
	}
}
