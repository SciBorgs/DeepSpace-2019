package main.java.frc.robot.subsystems;

import org.usfirst.frc.team1155.robot.Robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.PWMTalonSRX;
import edu.wpi.first.wpilibj.command.Subsystem;

import edu.wpi.first.networktables.NetworkTableInstance;

public class LimelightSubsystem extends Subsystem{

    public getCameraData(String variable){ // According to API, should get a given variable (x1,a2... etc.)
        NetworkTableInstance.getDefault().getTable("limelight").getEntry(variable).getDouble(0);
    }
    public setCameraParams(String param, int setting){ // According to API, should set a given param (camMode, pipeline... etc.)
        NetworkTableInstance.getDefault().getTable("limelight").getEntry(param).setNumber(setting);
    }

    @Override
    protected void initDefaultCommand() {
        // LITTERALLY DIE
    }
}