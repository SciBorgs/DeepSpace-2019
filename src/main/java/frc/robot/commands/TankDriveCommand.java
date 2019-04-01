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


public class TankDriveCommand extends Command {
    private final String fileName = "TankDriveCommand.java";
    private Joystick rightStick, leftStick;
    
    public TankDriveCommand() {
        rightStick = Robot.oi.rightStick;
        leftStick  = Robot.oi.leftStick;
    }

    @Override protected void initialize() {
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Initializing);
        Robot.driveSubsystem.setSpeedTank(0, 0);
    }
    @Override protected void execute() {
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Executing);
        Robot.driveSubsystem.setSpeed(leftStick, rightStick);
        //System.out.println("talon current: " + Robot.intakeSubsystem.intakeTalon.getOutputCurrent());
        //System.out.println("omega: " + Robot.positioningSubsystem.getAngularSpeed());
        //Robot.zLiftSubsystem.lift(Robot.driveSubsystem.processStick(leftStick));
        //Robot.positioningSubsystem.printPosition();
        //Robot.liftSubsystem.setArmTiltSpeed(Robot.driveSubsystem.processStick(leftStick));
        //Robot.liftSubsystem.setArmTiltSpeed(Robot.driveSubsystem.processStick(leftStick));
    }
        
    @Override protected boolean isFinished() {
        return false;
    }
    @Override protected void end() {
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Ending);
        Robot.driveSubsystem.setSpeedTank(0, 0);
    }
    @Override protected void interrupted() {
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Interrupted);
        end();
    }
}