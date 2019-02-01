package frc.robot.subsystems;

import frc.robot.PID;
import frc.robot.Robot;

import edu.wpi.first.wpilibj.command.Subsystem;

public class CargoFollowSubsystem extends Subsystem {
    
    public void followBall() {
        if (Robot.limelightSubsystem.getTableData(Robot.limelightSubsystem.getCameraTable(), "tv") != 1) {return;}
        double tx = Robot.limelightSubsystem.getTableData(Robot.limelightSubsystem.getCameraTable(), "tx");
        Robot.driveSubsystem.followBall(tx);
    }

    public void modeToCargo() {
        Robot.limelightSubsystem.setCameraParams("ledMode", 1); // Force LED Off
        Robot.limelightSubsystem.setCameraParams("pipeline", 9); // Switch to Cargo Pipeline
    }

    @Override
    protected void initDefaultCommand() {
        // LITTERALLY DIE
    }
}