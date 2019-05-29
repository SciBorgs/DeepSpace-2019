package frc.robot.commands;

import frc.robot.Robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.logging.Logger.CommandStatus;
import frc.robot.logging.Logger.DefaultValue;
import edu.wpi.first.wpilibj.command.InstantCommand;

public class TankDriveCommand extends InstantCommand {
    private final String FILE_NAME = "TankDriveCommand.java";
    private Joystick rightStick, leftStick;
    
    public TankDriveCommand() {
        this.rightStick = Robot.oi.rightStick;
        this.leftStick  = Robot.oi.leftStick;
    }

    @Override 
    protected void execute() {
	Robot.logger.logCommandStatus(FILE_NAME, CommandStatus.Executing);
        Robot.driveSubsystem.setSpeed(leftStick, rightStick);
    }   
}
