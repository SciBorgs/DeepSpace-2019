package frc.robot.helpers;

import frc.robot.Robot;

public class CargoFollowing {

    PID ballFollowerPIDCentered, ballFollowerPIDShifted;
    double ballFollowerP = 0.06, ballFollowerI = 0, ballFollowerD = 0.0065;
    double horizontalTheta = 27.;
    double verticalTheta = 20.5;

    public CargoFollowing() {
		ballFollowerPIDCentered = new PID(ballFollowerP, ballFollowerI, ballFollowerD);
		ballFollowerPIDShifted = new PID(ballFollowerP, ballFollowerI, ballFollowerD);
    }

    public void followBallShifted(double limelightShift) {
        double tx = Robot.limelightSubsystem.getTableData(Robot.limelightSubsystem.getCameraTable(), "tx");
        double ta = Robot.limelightSubsystem.getTableData(Robot.limelightSubsystem.getCameraTable(), "ta");
        double shift = limelightShift +  2./5. * Math.tan(tx) * Math.sqrt(ta*Math.tan(horizontalTheta)*Math.tan(verticalTheta));
        if (Robot.limelightSubsystem.getTableData(Robot.limelightSubsystem.getCameraTable(), "tv") == 1) {
            ballFollowerPIDShifted.add_measurement(shift);
        }
        System.out.println("tx" + tx);
        double turnMagnitude = ballFollowerPIDShifted.getOutput();
        Robot.driveSubsystem.setTurningPercentage(turnMagnitude);
    }

    public void followBallCenter(){
        double tx = Robot.limelightSubsystem.getTableData(Robot.limelightSubsystem.getCameraTable(), "tx");

    }    

    public void modeToCargo() {
        Robot.limelightSubsystem.setCameraParams("ledMode", 1); // Force LED Off
        Robot.limelightSubsystem.setCameraParams("pipeline", 9); // Switch to Cargo Pipeline
    }
}