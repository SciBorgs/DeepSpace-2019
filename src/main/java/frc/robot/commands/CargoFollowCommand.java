package frc.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.Robot;

public class CargoFollowCommand extends CommandGroup {

    public CargoFollowCommand() {
        requires(Robot.driveSubsystem);
        addParallel(new SuckCommand());
    }

    @Override protected void initialize() {
		System.out.println("following cargo");
        Robot.cargoFollowing.modeToCargo();
    }

	@Override protected void execute(){
		System.out.println("following cargo");
		if (Robot.oi.cargoFollowButton.get()){
			Robot.driveSubsystem.assistedDriveMode();
			Robot.cargoFollowing.followBall();
		} else {
			Robot.driveSubsystem.unassistedDriveMode();
		}
	}

	@Override protected boolean isFinished(){
		return !Robot.oi.suckButton.get();
		//return false;
	}

	@Override protected void end(){
		return;
	}

	@Override protected void interrupted(){
		end();
	}
}
