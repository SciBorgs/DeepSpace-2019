package frc.robot.helpers;

import frc.robot.Robot;

import edu.wpi.first.wpilibj.command.Subsystem;

public class CargoFollowing {

    PID ballFollowerPID;
    double ballFollowerP = 0.06;
    double ballFollowerI = 0;
    double ballFollowerD = 0.0065;
    double horizontalTheta = 27.;
    double verticalTheta = 20.5;

    public CargoFollowing() {
		ballFollowerPID = new PID(ballFollowerP, ballFollowerI, ballFollowerD);
    }

    public void followBall() {
        double tx = Robot.limelightSubsystem.getTableData(Robot.limelightSubsystem.getCameraTable(), "tx");
        double ta = Robot.limelightSubsystem.getTableData(Robot.limelightSubsystem.getCameraTable(), "ta");
        double shift = 2./5. * Math.tan(tx) * Math.sqrt(ta*Math.tan(horizontalTheta)*Math.tan(verticalTheta));
        if (Robot.limelightSubsystem.getTableData(Robot.limelightSubsystem.getCameraTable(), "tv") == 1) {
            ballFollowerPID.add_measurement(shift);
        }
        System.out.println("tx" + tx);
        double turnMagnitude = ballFollowerPID.getOutput();
        Robot.driveSubsystem.setTurningPercentage(turnMagnitude);
    }

    public void modeToCargo() {
        Robot.limelightSubsystem.setCameraParams("ledMode", 1); // Force LED Off
        Robot.limelightSubsystem.setCameraParams("pipeline", 9); // Switch to Cargo Pipeline
    }
}