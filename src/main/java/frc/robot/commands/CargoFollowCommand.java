package frc.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.Robot;
import frc.robot.logging.Logger.CommandStatus;

public class CargoFollowCommand extends CommandGroup {

    private final String FILENAME = "CargoFollowCommand.java";

    public CargoFollowCommand() {
        requires(Robot.driveSubsystem);
        addParallel(new SuckCommand());
    }

    @Override
    protected void initialize() {
	Robot.logger.logCommandStatus(FILENAME, CommandStatus.Initializing);
	Robot.following.resetCargoPID();
    }
	
    @Override
    protected void execute(){
	Robot.following.modeToCargo();
	Robot.logger.logCommandStatus(FILENAME, CommandStatus.Executing);
	if (Robot.oi.cargoFollowButton.get()){
		Robot.driveSubsystem.assistedDriveMode();
		Robot.following.followBall();
	} else { Robot.driveSubsystem.manualDriveMode(); }
    }

    @Override
    protected boolean isFinished() { return !Robot.oi.suckButton.get(); }

    @Override
    protected void end(){
	Robot.logger.logCommandStatus(FILENAME, CommandStatus.Ending);
	Robot.driveSubsystem.manualDriveMode();
    }

    @Override
    protected void interrupted(){
	Robot.logger.logCommandStatus(FILENAME, CommandStatus.Interrupted);
	end();
    }
}
