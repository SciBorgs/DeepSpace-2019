package frc.robot.subsystems;

import frc.robot.PID;
import frc.robot.Robot;

import edu.wpi.first.wpilibj.command.Subsystem;

public class CargoFollowSubsystem extends Subsystem {
    
    public void followBall() {
        if (Robot.limelight.getTableData(Robot.limelight.getCameraTable(), "tv") != 1) {return;}
        double tx = Robot.limelight.getTableData(Robot.limelight.getCameraTable(), "tx");
        Robot.driveSubsystem.followBall(tx);
    }

    public void ballLimelightMode() {
        Robot.limelight.setCameraParams("ledMode", 1); // Force LED Off
        Robot.limelight.setCameraParams("pipeline", 9); // Switch to Ball Pipeline
    }

    @Override
    protected void initDefaultCommand() {
        // LITTERALLY DIE
    }
}