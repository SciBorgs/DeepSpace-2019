package frc.robot.commands;

import frc.robot.Robot;
import frc.robot.subsystems.LiftSubsystem.Target;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import edu.wpi.first.wpilibj.command.InstantCommand;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Command;

public class LiftCommand extends Command {

	private final String fileName = "LiftCommand.java";

	private boolean lastStatic;

	public LiftCommand() {
		requires(Robot.liftSubsystem);
		lastStatic = false;
	}

	@Override protected void initialize(){
		Robot.liftSubsystem.autoArmMode();
	}

	@Override protected void execute(){
		System.out.println("lifting command");
		//Robot.liftSubsystem.moveToTarget(Robot.liftSubsystem.getTarget());
		Robot.liftSubsystem.moveArmToTarget(Robot.liftSubsystem.getTarget());
	}

	@Override protected boolean isFinished(){
		if (lastStatic){
			return Robot.liftSubsystem.isStatic();
		} else {
			lastStatic = Robot.liftSubsystem.isStatic();
			return false;
		}
	}

	@Override protected void end(){
		Robot.liftSubsystem.manualArmMode();
	}

	@Override protected void interrupted(){
		return;
	}
}