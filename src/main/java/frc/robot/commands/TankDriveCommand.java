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
import frc.robot.subsystems.IntakeSubsystem.HatchDepositControl;
import frc.robot.subsystems.IntakeSubsystem.IntakeMode;

public class TankDriveCommand extends Command {
    private Joystick rightStick, leftStick;
    
    public TankDriveCommand() {
        rightStick = Robot.oi.rightStick;
        leftStick  = Robot.oi.leftStick;
    }

    @Override protected void initialize() {
        Robot.driveSubsystem.setSpeedTank(0, 0);
    }
    @Override protected void execute() {
        Robot.driveSubsystem.setSpeedRaw(leftStick, rightStick);
        //Robot.liftSubsystem.setArmTiltSpeed(Robot.driveSubsystem.processStick(leftStick));
    }
        
    @Override protected boolean isFinished() {
        return false;
    }
    @Override protected void end() {
        Robot.driveSubsystem.setSpeedTank(0, 0);
    }
    @Override protected void interrupted() {
        end();
    }
}