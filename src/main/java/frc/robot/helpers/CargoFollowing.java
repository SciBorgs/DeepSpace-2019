package frc.robot.helpers;

import frc.robot.Robot;

public class CargoFollowing {
	private final String fileName = "DriveSubsystem.java";

    PID ballFollowerPID, lineupPID;
    public final static double ballFollowerP = 0.062, ballFollowerI = 0, ballFollowerD = 0.00215;
    public final static double lineupP = 0.035, lineupI = 0, lineupD = 0.00;
    public final static int lineupSmoother = 5;
    public final static double LINEUP_SHIFT = -.95;

    public CargoFollowing() {
        resetCargoPID();
        resetLineupPID();
        Robot.logger.logFinalField(this.fileName, "ballFollowerP", ballFollowerP);
        Robot.logger.logFinalField(this.fileName, "ballFollowerD", ballFollowerD);
    }

    public PID getPid() {
        return ballFollowerPID;
    }

    public void resetCargoPID(){
        ballFollowerPID = new PID(ballFollowerP, ballFollowerI, ballFollowerD);
    }
    public void resetLineupPID(){
        lineupPID = new PID(lineupP, lineupI, lineupD);
        lineupPID.setSmoother(lineupSmoother);
    }

    public void followBallShifted() {
        double tx = Robot.limelightSubsystem.getTableData(Robot.limelightSubsystem.getCameraTable(), "tx");
        double ta = Robot.limelightSubsystem.getTableData(Robot.limelightSubsystem.getCameraTable(), "ta");
        double shift = 2./5. * Math.tan(tx) * Math.sqrt(ta*Math.tan(Robot.limelightSubsystem.IMAGE_WIDTH)*Math.tan(Robot.limelightSubsystem.IMAGE_HEIGHT)) + Robot.limelightSubsystem.SHIFT;
        if (Robot.limelightSubsystem.getTableData(Robot.limelightSubsystem.getCameraTable(), "tv") == 1) {
            ballFollowerPID.add_measurement(shift);
        }
        System.out.println("tx" + tx);
        double turnMagnitude = ballFollowerPID.getOutput();
        Robot.driveSubsystem.setTurningPercentage(turnMagnitude);
        
    }

    public void followBallCentered(){
    }
    public void followObject(PID pid, double tx){
        if (Robot.limelightSubsystem.getTableData(Robot.limelightSubsystem.getCameraTable(), "tv") == 1) {
            System.out.println("tx: " + tx);
            pid.add_measurement(tx);
        }
        double turnMagnitude = pid.getOutput();
        System.out.println("turn mag:" + turnMagnitude);
        Robot.driveSubsystem.setTurningPercentage(turnMagnitude);  
    }

    public void lineup(){
        modeToLineup();
        //double tx = Robot.limelightSubsystem.getTableData(Robot.limelightSubsystem.getCameraTable(), "tx");
        double txRadians = RetroreflectiveDetection.extractData().get("angle");
        double tx = Math.toDegrees(txRadians);
        tx += LINEUP_SHIFT;
        System.out.println("tx: " + tx);
        followObject(lineupPID, tx);
    }

    public void followBall(){
        modeToCargo();
        double tx = Robot.limelightSubsystem.getTableData(Robot.limelightSubsystem.getCameraTable(), "tx");
        if (Robot.limelightSubsystem.SHIFT == 0){
            followObject(ballFollowerPID, tx);
        } else {
            followBallShifted();
        }
    }

    public void periodicLog(){
    }

    public void modeToCargo() {
        Robot.limelightSubsystem.setCameraParams("pipeline", 9); // Switch to Cargo Pipeline
        double pipe = Robot.limelightSubsystem.getTableData(Robot.limelightSubsystem.getCameraTable(), "getpipe");
        System.out.println("Pipe: " + pipe);
    }
    
    public void modeToLineup() {
        Robot.limelightSubsystem.setCameraParams("pipeline", 0); // Switch to Cargo Pipeline
    }
}