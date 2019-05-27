package frc.robot.commands;

import frc.robot.Robot;
import frc.robot.subsystems.LiftSubsystem.Target;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import edu.wpi.first.wpilibj.command.InstantCommand;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.logging.Logger.CommandStatus;
import frc.robot.logging.Logger.DefaultValue;

public class LiftCommand extends Command {

	private final String fileName = "LiftCommand.java";

	private boolean lastStatic;

	public LiftCommand() {
		requires(Robot.liftSubsystem);
		lastStatic = false;
	}

	@Override protected void initialize(){
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Initializing);
		Robot.liftSubsystem.autoArmMode();
		Robot.liftSubsystem.currentlyTiltingArm();
		// Robot.liftSubsystem.autoCascadeMode(); // Comment for regular auto-carriage
	}

	@Override protected void execute(){
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Executing);
		//Robot.liftSubsystem.moveToTarget(Robot.liftSubsystem.getTarget()); // Comment for regular auto-carriage
		Robot.liftSubsystem.moveArmToTarget(Robot.liftSubsystem.getTarget()); // Uncomment for regular auto-carriage
	}

	@Override protected boolean isFinished(){
		/*if (lastStatic){
			return Robot.liftSubsystem.isArmStatic();
		} else {
			lastStatic = Robot.liftSubsystem.isArmStatic();
			return false;
		}*/
		return false;
	}

	@Override protected void end(){
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Ending);
		Robot.liftSubsystem.manualArmMode();
		Robot.liftSubsystem.manualCascadeMode();
	}

	@Override protected void interrupted(){
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Interrupted);
		return;
	}
}