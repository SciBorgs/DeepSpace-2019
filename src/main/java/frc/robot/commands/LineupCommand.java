package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.logging.Logger.CommandStatus;
import frc.robot.logging.Logger.DefaultValue;

public class LineupCommand extends Command {

    private final String fileName = "LineupCommand.java";
    
    public LineupCommand(){}

    @Override protected void initialize() {
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Initializing);
		Robot.following.resetLineupPID();
        //Robot.gearShiftSubsystem.shiftUp();
    }


    @Override protected void execute(){
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Executing);
        //Robot.lineup.simpleResetInfo();
        //Robot.lineup.move();
        System.out.println("lining up");
        Robot.following.lineup();
        Robot.driveSubsystem.assistedDriveMode();
    }

	@Override protected boolean isFinished(){
		return !Robot.oi.lineupButton.get();
		//return false;
	}

	@Override protected void end(){
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Ending);
        //Robot.gearShiftSubsystem.shiftDown();
        Robot.driveSubsystem.manualDriveMode();
        Robot.following.modeToCargo();
		//return;
	}

	@Override protected void interrupted(){
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Interrupted);
		end();
	}
}
