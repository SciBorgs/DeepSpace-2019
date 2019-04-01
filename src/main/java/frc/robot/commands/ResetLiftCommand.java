package frc.robot.commands;

import frc.robot.Robot;
import frc.robot.subsystems.LiftSubsystem.Target;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.command.Command;

public class ResetLiftCommand extends Command {
	private final String fileName = "ResetLiftCommand.java";

	public ResetLiftCommand() {
		requires(Robot.liftSubsystem);
	}

	@Override protected void initialize(){
		return;
	}

	@Override protected void execute(){
		Robot.liftSubsystem.moveToTarget(Target.Initial);
	}

	@Override protected boolean isFinished(){
		return Robot.liftSubsystem.isStatic();
	}

	@Override protected void end(){
		return;
	}

	@Override protected void interrupted(){
		return;
	}
}