package frc.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.Robot;
import frc.robot.logging.Logger.DefaultValue;
import frc.robot.logging.Logger.CommandStatus;


public class StopIntakeCommand extends CommandGroup {
	private final String fileName = "StopIntakeCommand.java";
    public static final double DELAY = .3; // In seconds

    public StopIntakeCommand() {
        requires(Robot.driveSubsystem);
        addSequential(new CloseArmCommand());
        addSequential(new StopIntakeWheelsCommand(), DELAY);
    }

    @Override protected void initialize() {
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Initializing);
    }

	@Override protected void execute(){
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Executing);
        return;
	}

	@Override protected boolean isFinished(){
		return true;
	}

	@Override protected void end(){
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Ending);
		return;
	}

	@Override protected void interrupted(){
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Interrupted);
		end();
	}
}