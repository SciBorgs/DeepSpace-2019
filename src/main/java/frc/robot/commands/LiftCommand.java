package frc.robot.commands;

import frc.robot.Robot;
import frc.robot.subsystems.LiftSubsystem.Target;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class LiftCommand extends InstantCommand {
	public LiftCommand() {
		requires(Robot.liftSubsystem);
	}

	@Override
	protected void execute(){
		Robot.liftSubsystem.moveToHeight(Robot.liftSubsystem.getTarget());
	}
}