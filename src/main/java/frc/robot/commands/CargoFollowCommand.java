package frc.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.Robot;
import frc.robot.logging.Logger.DefaultValue;

public class CargoFollowCommand extends CommandGroup {

	private final String fileName = "CargoFollowCommand.java";

    public CargoFollowCommand() {
        requires(Robot.driveSubsystem);
        addParallel(new SuckCommand());
    }

    @Override protected void initialize() {
		Robot.logger.addData(this.fileName, Robot.logger.commandStatus, "initializing", DefaultValue.Empty);
		System.out.println("following cargo");
		Robot.cargoFollowing.modeToCargo();
        Robot.gearShiftSubsystem.shiftUp();
    }

	@Override protected void execute(){
		Robot.logger.addData(this.fileName, Robot.logger.commandStatus, "executing", DefaultValue.Empty);
		System.out.println("following cargo");
		if (Robot.oi.cargoFollowButton.get()){
			Robot.driveSubsystem.assistedDriveMode();
			Robot.cargoFollowing.followBall();
		} else {
			Robot.driveSubsystem.manualDriveMode();
		}
	}

	@Override protected boolean isFinished(){
		return !Robot.oi.suckButton.get();
		//return false;
	}

	@Override protected void end(){
		Robot.logger.addData(this.fileName, Robot.logger.commandStatus, "ending", DefaultValue.Empty);
		Robot.gearShiftSubsystem.shiftDown();
		Robot.driveSubsystem.manualDriveMode();
		//return;
	}

	@Override protected void interrupted(){
		Robot.logger.addData(this.fileName, Robot.logger.commandStatus, "interrupted", DefaultValue.Empty);
		end();
	}
}
