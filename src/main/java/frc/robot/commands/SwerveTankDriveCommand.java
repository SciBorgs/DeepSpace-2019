package frc.robot.commands;

import frc.robot.Robot;

import frc.robot.logging.Logger.CommandStatus;
import edu.wpi.first.wpilibj.command.InstantCommand;

public class SwerveTankDriveCommand extends InstantCommand {
    private final String FILENAME = "SwerveTankDriveCommand.java";
   
    
    public SwerveTankDriveCommand(){}

    @Override protected void execute() {
        Robot.logger.logCommandStatus(FILENAME, CommandStatus.Executing);

        double forward = Robot.driveSubsystem.processStick(Robot.oi.leftStick);
        double turnAmount = Robot.oi.rightStick.getX();
        Robot.driveSubsystem.setSpeedTankForwardTurningPercentage(forward, turnAmount);

        
    }
}
