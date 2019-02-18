package frc.robot.commands;

import frc.robot.Robot;
import frc.robot.subsystems.LiftSubsystem.Target;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class LiftCommand extends InstantCommand {
	private Target target;

	public LiftCommand(Target target) {
		requires(Robot.liftSubsystem);
		this.target = target;
	}

	@Override
	protected void execute(){
		Robot.liftSubsystem.moveToHeight(target);
	}
}