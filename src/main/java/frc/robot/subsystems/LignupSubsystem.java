package frc.robot.subsystems;

import java.util.ArrayList;
import java.util.Hashtable;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.Robot;
import frc.robot.PID;

public class LignupSubsystem extends Subsystem {

    private PID pid = new PID(0.05,0,0);
    private double desiredX = 0.5;
    private double defaultSpeed = 0.3;

    public LignupSubsystem() {}

    public move(){
        pid.add_measurement(desiredX - Robot.positioning.getX());
        Robot.driveSubsystem.setSpeedTank(defaultSpeed + pid.getOutput(), rightSpeed - pid.getOutput());
    }

    @Override
    protected void initDefaultCommand() {
        // LITTERALLY DIE
    }
}
