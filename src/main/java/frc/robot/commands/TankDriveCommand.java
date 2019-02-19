package frc.robot.commands;

import frc.robot.Robot;
import frc.robot.subsystems.LiftSubsystem.Target;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.Command;

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
        //Robot.driveSubsystem.setSpeed(leftStick, rightStick);
        //System.out.println("angle: " + Robot.positioningSubsystem.getPigeon().getAngle());
        System.out.println("DI cargo: " + Robot.intakeSubsystem.holdingCargoSecure());
        System.out.println("DI hatch: " + Robot.intakeSubsystem.holdingHatch());
    }
        
    @Override protected boolean isFinished() {
        return false;}
    @Override protected void end() {
        Robot.driveSubsystem.setSpeedTank(0, 0);
    }
    @Override protected void    interrupted() {
        end();
    }
}
