package frc.robot.commands;

import frc.robot.Robot;
import frc.robot.subsystems.LiftSubsystem.Target;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import edu.wpi.first.wpilibj.command.InstantCommand;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Command;

public class LiftCommand extends Command {
	public LiftCommand() {
		requires(Robot.liftSubsystem);
	}

	@Override protected void initialize(){
		return;
	}

	@Override protected void execute(){
		System.out.println("lifting command");
		//Robot.liftSubsystem.moveToTarget(Robot.liftSubsystem.getTarget());
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