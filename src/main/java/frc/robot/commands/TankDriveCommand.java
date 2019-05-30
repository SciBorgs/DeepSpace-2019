package frc.robot.commands;

import frc.robot.Robot;
import frc.robot.logging.Logger.CommandStatus;
import edu.wpi.first.wpilibj.command.InstantCommand;

public class TankDriveCommand extends InstantCommand {
    private final String FILENAME = "TankDriveCommand.java";

    @Override 
    protected void execute() {
	Robot.logger.logCommandStatus(FILENAME, CommandStatus.Executing);
        Robot.driveSubsystem.setSpeed(Robot.oi.leftStick, Robot.oi.rightStick);
    }   
}
