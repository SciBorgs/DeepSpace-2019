package frc.robot.commands;

import frc.robot.Robot;
import frc.robot.subsystems.LiftSubsystem.Target;

import edu.wpi.first.wpilibj.buttons.JoystickButton;

import edu.wpi.first.wpilibj.command.Command;

public class LiftCommand extends Command {
	private Target target;
	

	public LiftCommand(Target target) {
		requires(Robot.liftSubsystem);
		this.target = target;
	}

	@Override
	protected boolean isFinished() {
		return true;
	}
	@Override
	protected void initialize(){
		Robot.liftSubsystem.moveToHeight(target);
	}
	
	@Override
	protected void execute() {
		return;
	}
}