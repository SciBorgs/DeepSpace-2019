package frc.robot.commands;

import frc.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.logging.Logger.CommandStatus;

public class LiftCommand extends Command {

	private final String FILENAME = "LiftCommand.java";

	public LiftCommand() {
		requires(Robot.liftSubsystem);
	}

	@Override protected void initialize(){
		Robot.logger.logCommandStatus(FILENAME, CommandStatus.Initializing);
		Robot.liftSubsystem.autoArmMode();
		Robot.liftSubsystem.currentlyTiltingArm();
	}

	@Override protected void execute(){
		Robot.logger.logCommandStatus(FILENAME, CommandStatus.Executing);
		Robot.liftSubsystem.moveArmToTarget(Robot.liftSubsystem.getTarget()); 
	}

	@Override protected boolean isFinished(){ return false; }

	@Override protected void end(){
		Robot.logger.logCommandStatus(FILENAME, CommandStatus.Ending);
		Robot.liftSubsystem.manualArmMode();
		Robot.liftSubsystem.manualCascadeMode();
	}

	@Override protected void interrupted(){
		Robot.logger.logCommandStatus(FILENAME, CommandStatus.Interrupted);
		end();
	}
}
