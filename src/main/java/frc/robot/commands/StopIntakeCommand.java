package frc.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.Robot;

public class StopIntakeCommand extends CommandGroup {
	private final String fileName = "StopIntakeCommand.java";
    public static final double DELAY = .3; // In seconds

    public StopIntakeCommand() {
        requires(Robot.driveSubsystem);
        addSequential(new CloseArmCommand());
        addSequential(new StopIntakeWheelsCommand(), DELAY);
    }

    @Override protected void initialize() {
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
		end();
	}
}