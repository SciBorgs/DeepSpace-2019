package frc.robot.commands;

import frc.robot.Robot;
import frc.robot.subsystems.LiftSubsystem.Target;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import edu.wpi.first.wpilibj.command.InstantCommand;

public class ConditionalLiftCommand extends ConditionalCommand {
	public ConditionalLiftCommand() {
		super(new LiftCommand(), new SlamDownCommand());
        requires(Robot.liftSubsystem);
	}

	@Override protected boolean condition() {
		return Robot.liftSubsystem.getTarget() != Target.Slam;
	}
}

class LiftCommand extends InstantCommand {
	public LiftCommand() {
		requires(Robot.liftSubsystem);
	}

	@Override protected void execute(){
		Robot.liftSubsystem.moveToTargetHeight(Robot.liftSubsystem.getTarget());
	}
}

class SlamDownCommand extends InstantCommand {
	public SlamDownCommand() {
		requires(Robot.liftSubsystem);
	}

	@Override protected void execute(){
		Robot.liftSubsystem.pickupHatchFromGround();
	}
}