package frc.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.Robot;
import frc.robot.logging.Logger.CommandStatus;
import frc.robot.logging.Logger.DefaultValue;

public class CargoFollowCommand extends CommandGroup {

	private final String fileName = "CargoFollowCommand.java";

    public CargoFollowCommand() {
        requires(Robot.driveSubsystem);
        addParallel(new SuckCommand());
    }

    @Override protected void initialize() {
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Initializing);
		System.out.println("following cargo");
		Robot.cargoFollowing.resetCargoPID();
        //Robot.gearShiftSubsystem.shiftUp();
    }

	@Override protected void execute(){
		Robot.cargoFollowing.modeToCargo();
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Executing);
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
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Ending);
		//Robot.gearShiftSubsystem.shiftDown();
		Robot.driveSubsystem.manualDriveMode();
		//return;
	}

	@Override protected void interrupted(){
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Interrupted);
		end();
	}
}
