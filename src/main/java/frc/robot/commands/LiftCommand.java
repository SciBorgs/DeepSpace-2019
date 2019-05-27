package frc.robot.commands;

import frc.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.logging.Logger.CommandStatus;

public class LiftCommand extends Command {

	private final String fileName = "LiftCommand.java";

	private boolean lastStatic;

	public LiftCommand() {
		requires(Robot.liftSubsystem);
		lastStatic = false;
	}

	@Override protected void initialize(){
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Initializing);
		Robot.liftSubsystem.autoArmMode();
		Robot.liftSubsystem.currentlyTiltingArm();
	}

	@Override protected void execute(){
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Executing);
		Robot.liftSubsystem.moveArmToTarget(Robot.liftSubsystem.getTarget()); // Uncomment for regular auto-carriage
	}

	@Override protected boolean isFinished(){
		return false;
	}

	@Override protected void end(){
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Ending);
		Robot.liftSubsystem.manualArmMode();
		Robot.liftSubsystem.manualCascadeMode();
	}

	@Override protected void interrupted(){
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Interrupted);
		end();
		return;
	}
}