package frc.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.Robot;
import frc.robot.logging.Logger.DefaultValue;


public class StopIntakeCommand extends CommandGroup {
	private final String fileName = "StopIntakeCommand.java";
    public static final double DELAY = .3; // In seconds

    public StopIntakeCommand() {
        requires(Robot.driveSubsystem);
        addSequential(new CloseArmCommand());
        addSequential(new StopIntakeWheelsCommand(), DELAY);
    }

    @Override protected void initialize() {
		Robot.logger.addData(this.fileName, Robot.logger.commandStatus, "initializing", DefaultValue.Empty);
    }

	@Override protected void execute(){
		Robot.logger.addData(this.fileName, Robot.logger.commandStatus, "executing", DefaultValue.Empty);
        return;
	}

	@Override protected boolean isFinished(){
		return true;
	}

	@Override protected void end(){
		Robot.logger.addData(this.fileName, Robot.logger.commandStatus, "ending", DefaultValue.Empty);
		return;
	}

	@Override protected void interrupted(){
		Robot.logger.addData(this.fileName, Robot.logger.commandStatus, "interrupted", DefaultValue.Empty);
		end();
	}
}