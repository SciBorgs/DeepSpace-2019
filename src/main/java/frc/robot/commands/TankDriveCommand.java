package frc.robot.commands;

import frc.robot.Robot;
import frc.robot.Utils;
import frc.robot.subsystems.LiftSubsystem.Target;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

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
        //Robot.driveSubsystem.setSpeed(leftStick, rightStick);
        //Robot.positioningSubsystem.printPosition();
        //System.out.println("raw: " + Robot.positioningSubsystem.getSparkAngle(Robot.driveSubsystem.lm));
        if (Robot.driveSubsystem.processStick(leftStick) == 0 && Robot.driveSubsystem.processStick(rightStick) == 0){
            Robot.liftSubsystem.moveToHeight(Utils.inchesToMeters(30));
        } else {
            Robot.liftSubsystem.setLiftSpeed(Robot.driveSubsystem.processStick(leftStick));
            Robot.liftSubsystem.setArmTiltSpeed(Robot.driveSubsystem.processStick(rightStick));
        }
        Robot.positioningSubsystem.printPosition();
        //System.out.println("DI cargo: " + Robot.intakeSubsystem.holdingCargoSecure());
        //System.out.println("DI hatch: " + Robot.intakeSubsystem.holdingHatch());
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