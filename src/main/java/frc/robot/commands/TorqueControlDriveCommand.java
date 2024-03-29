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

public class TorqueControlDriveCommand extends InstantCommand {
    private final String fileName = "TorqueControlDriveCommand.java";
    private Joystick rightStick, leftStick;
    
    public TorqueControlDriveCommand() {
        leftStick  = Robot.oi.leftStick;
        rightStick = Robot.oi.rightStick;
    }

    @Override protected void execute() {
        Robot.logger.logCommandStatus(this.fileName, CommandStatus.Executing);

        double forward = Robot.driveSubsystem.processStick(this.leftStick);
        double turn = Robot.driveSubsystem.deadzone(this.rightStick.getX()) * .7    ;
        Robot.driveSubsystem.setSpeedTankForwardTorque(forward, turn);
        //System.out.println("talon current: " + Robot.intakeSubsystem.intakeTalon.getOutputCurrent());
        //System.out.println("omega: " + Robot.positioningSubsystem.getAngularSpeed());
        //Robot.zLiftSubsystem.lift(Robot.driveSubsystem.processStick(leftStick));
        //Robot.positioningSubsystem.printPosition();
        //Robot.liftSubsystem.setArmTiltSpeed(Robot.driveSubsystem.processStick(leftStick));
        //Robot.liftSubsystem.setArmTiltSpeed(Robot.driveSubsystem.processStick(leftStick));
    }
}