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

public class SwerveTankDriveCommand extends InstantCommand {
    private final String fileName = "TankDriveCommand.java";
    private Joystick rightStick, leftStick;
    
    public SwerveTankDriveCommand() {
        leftStick  = Robot.oi.leftStick;
    }

    @Override protected void execute() {
        Robot.logger.logCommandStatus(this.fileName, CommandStatus.Executing);

        double forward = Robot.driveSubsystem.processStick(this.leftStick);
        Robot.driveSubsystem.setSpeedTankForwardTurningPercentage(forward, this.leftStick.getX());

        
        //System.out.println("talon current: " + Robot.intakeSubsystem.intakeTalon.getOutputCurrent());
        //System.out.println("omega: " + Robot.positioningSubsystem.getAngularSpeed());
        //Robot.zLiftSubsystem.lift(Robot.driveSubsystem.processStick(leftStick));
        //Robot.positioningSubsystem.printPosition();
        //Robot.liftSubsystem.setArmTiltSpeed(Robot.driveSubsystem.processStick(leftStick));
        //Robot.liftSubsystem.setArmTiltSpeed(Robot.driveSubsystem.processStick(leftStick));
    }
}