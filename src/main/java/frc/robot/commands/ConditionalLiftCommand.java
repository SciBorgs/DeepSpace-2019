package frc.robot.commands;

import frc.robot.Robot;
import frc.robot.subsystems.LiftSubsystem.Target;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.command.Command;


public class ConditionalLiftCommand extends ConditionalCommand {
	public ConditionalLiftCommand() {
		super(new LiftCommand(), new SlamDownCommand());
        requires(Robot.liftSubsystem);
	}

	@Override protected boolean condition() {
		return Robot.liftSubsystem.getTarget() != Target.Slam;
	}
}

class LiftCommand extends Command {
	public LiftCommand() {
		requires(Robot.liftSubsystem);
	}

	@Override protected void initialize(){
		return;
	}

	@Override protected void execute(){
		Robot.liftSubsystem.moveToTargetHeight(Robot.liftSubsystem.getTarget());
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

class SlamDownCommand extends Command {
	public SlamDownCommand() {
		requires(Robot.liftSubsystem);
	}

	@Override protected void initialize(){
		return;
	}

	@Override protected void execute(){
		Robot.liftSubsystem.pickupHatchFromGround();
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