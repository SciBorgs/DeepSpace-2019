package frc.robot.commands;

import frc.robot.Robot;
import frc.robot.Utils;
import frc.robot.subsystems.LiftSubsystem.Target;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.ctre.phoenix.motorcontrol.ControlMode;


import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.logging.Logger.CommandStatus;
import frc.robot.logging.Logger.DefaultValue;
import edu.wpi.first.wpilibj.command.InstantCommand;

public class TankDriveCommand extends InstantCommand {
    private final String fileName = "TankDriveCommand.java";
    private Joystick rightStick, leftStick;
    
    public TankDriveCommand() {
        this.rightStick = Robot.oi.rightStick;
        this.leftStick  = Robot.oi.leftStick;
    }

    @Override protected void execute() {
	Robot.logger.logCommandStatus(this.fileName, CommandStatus.Executing);
        Robot.driveSubsystem.setSpeed(leftStick, rightStick);
        
        System.out.println("left stick current: " + leftStick);
	System.out.println("right stick current: " + rightStick);
    }
        
}
