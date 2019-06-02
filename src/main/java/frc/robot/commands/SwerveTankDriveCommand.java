package frc.robot.commands;

import frc.robot.Robot;

import edu.wpi.first.wpilibj.Joystick;
import frc.robot.logging.Logger.CommandStatus;
import edu.wpi.first.wpilibj.command.InstantCommand;

public class SwerveTankDriveCommand extends InstantCommand {
    private final String FILENAME = "SwerveTankDriveCommand.java";
    private Joystick leftStick = Robot.oi.leftStick;
    private Joystick rightStick = Robot.oi.rightStick;

    
    public SwerveTankDriveCommand(){}

    @Override protected void execute() {
        Robot.logger.logCommandStatus(FILENAME, CommandStatus.Executing);

        double forward = Robot.driveSubsystem.processStick(this.leftStick);
        Robot.driveSubsystem.setSpeedTankForwardTurningPercentage(forward, this.rightStick.getX());

        
    }
}
