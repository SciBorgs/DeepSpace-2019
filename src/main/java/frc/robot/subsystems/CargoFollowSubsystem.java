package frc.robot.subsystems;

import frc.robot.PID;
import frc.robot.Robot;

import edu.wpi.first.wpilibj.command.Subsystem;

public class CargoFollowSubsystem extends Subsystem {

    PID ballFollowerPID;
    double ballFollowerP = 0.06;
    double ballFollowerI = 0;
    double ballFollowerD = 0.0065;  

    public CargoFollowSubsystem() {
		ballFollowerPID = new PID(ballFollowerP, ballFollowerI, ballFollowerD);
    }

    public void followBall() {
        double tx = Robot.limelightSubsystem.getTableData(Robot.limelightSubsystem.getCameraTable(), "tx");
        if (Robot.limelightSubsystem.getTableData(Robot.limelightSubsystem.getCameraTable(), "tv") == 1) {
            ballFollowerPID.add_measurement(tx);
        }
        System.out.println("tx" + tx);
        double turnMagnitude = ballFollowerPID.getOutput();
        Robot.driveSubsystem.setTurningPercentage(turnMagnitude);
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