package frc.robot.commands;

import frc.robot.Robot;
import frc.robot.commands.*;
import frc.robot.commands.LevelCounterUpdateCommand.LevelChange;
import frc.robot.subsystems.LiftSubsystem.Target;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class PickupHatchFromGroundCommand extends CommandGroup {
    private final double SCOOP_UP_TO_SECURE_TIMEOUT   = .1;
    private final double SECURE_TO_SCOOP_DOWN_TIMEOUT = .1;

	public PickupHatchFromGroundCommand() {
        requires(Robot.liftSubsystem);
        addSequential(new LevelCounterUpdateCommand(LevelChange.Up));
        addSequential(new LiftCommand());
        addSequential(new ScoopUpCommand(), SCOOP_UP_TO_SECURE_TIMEOUT);
        addSequential(new SecureHatchCommand(), SECURE_TO_SCOOP_DOWN_TIMEOUT);
        addSequential(new ScoopDownCommand());
	}

	@Override protected void initialize(){
		return;
	}

	@Override protected void execute(){
        return;
	}

	@Override protected boolean isFinished(){
		return true;
	}

	@Override protected void end(){
		return;
	}

	@Override protected void interrupted(){
		return;
	}
}