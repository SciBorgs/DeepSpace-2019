package frc.robot.commands;

import frc.robot.Robot;
import frc.robot.subsystems.LiftSubsystem.Target;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.logging.Logger.DefaultValue;

public class ResetLiftCommand extends Command {
	private final String fileName = "ResetLiftCommand.java";

	public ResetLiftCommand() {
		requires(Robot.liftSubsystem);
	}

	@Override protected void initialize(){
        Robot.logger.addData(this.fileName, Robot.logger.commandStatus, "initializing", DefaultValue.Empty);
		return;
	}

	@Override protected void execute(){
        Robot.logger.addData(this.fileName, Robot.logger.commandStatus, "executing", DefaultValue.Empty);
		Robot.liftSubsystem.moveToTarget(Target.Initial);
	}

	@Override protected boolean isFinished(){
		return Robot.liftSubsystem.isStatic();
	}

	@Override protected void end(){
        Robot.logger.addData(this.fileName, Robot.logger.commandStatus, "ending", DefaultValue.Empty);
		return;
	}

	@Override protected void interrupted(){
        Robot.logger.addData(this.fileName, Robot.logger.commandStatus, "interrupted", DefaultValue.Empty);
		return;
	}
}