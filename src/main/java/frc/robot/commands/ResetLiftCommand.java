package frc.robot.commands;

import frc.robot.Robot;
import frc.robot.subsystems.LiftSubsystem.Target;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.logging.Logger.CommandStatus;
import frc.robot.logging.Logger.DefaultValue;

public class ResetLiftCommand extends Command {
	private final String fileName = "ResetLiftCommand.java";

	public ResetLiftCommand() {
		requires(Robot.liftSubsystem);
	}

	@Override protected void initialize(){
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Initializing);
	}

	@Override protected void execute(){
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Executing);
		Robot.liftSubsystem.moveToTarget(Target.Initial);
	}

	@Override protected boolean isFinished(){
		return Robot.liftSubsystem.isStatic();
	}

	@Override protected void end(){
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Ending);
	}

	@Override protected void interrupted(){
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Interrupted);
		return;
	}
}