package frc.robot.helpers;

import frc.robot.Robot;

public class Following {
	private final String fileName = "DriveSubsystem.java";

    PID ballFollowerPID, lineupPID;
    public final static double ballFollowerP = 0.062, ballFollowerI = 0, ballFollowerD = 0.00215;
    public final static double lineupP = 0.035, lineupI = 0, lineupD = 0.00;
    public final static int lineupSmoother = 5;
    public final static double LINEUP_SHIFT = -.95;

    public Following() {
        ballFollowerPID = new PID(ballFollowerP, ballFollowerI, ballFollowerD);
        lineupPID = new PID(lineupP, lineupI, lineupD);
        Robot.logger.logFinalField(this.fileName, "ballFollowerP", ballFollowerP);
        Robot.logger.logFinalField(this.fileName, "ballFollowerD", ballFollowerD);
    }

    public PID getPid() {
        return ballFollowerPID;
    }

    public void resetCargoPID() {ballFollowerPID.reset();}
    public void resetLineupPID(){lineupPID.reset();}

    public void followObject(PID pid, double tx){
        if (Robot.limelightSubsystem.contourExists()){
            pid.add_measurement(tx);
        }
        double turnMagnitude = pid.getOutput();
        Robot.driveSubsystem.setTurningPercentage(turnMagnitude);  
    }

    public void lineup(){
        modeToLineup();
        double txRadians = RetroreflectiveDetection.extractData().get("angle");
        // The lineup_shift is to correct for the intake being off-centered
        double tx = Math.toDegrees(txRadians) + LINEUP_SHIFT;
        followObject(lineupPID, tx);
    }

    public void followBall(){
        modeToCargo();
        double tx = Robot.limelightSubsystem.getTableData(Robot.limelightSubsystem.getCameraTable(), "tx");
        followObject(ballFollowerPID, tx);
    }

    public void periodicLog(){
    }

    public void modeToCargo() {
        Robot.limelightSubsystem.setCameraParams("pipeline", 9); // Switch to Cargo Pipeline
    }
    
    public void modeToLineup() {
        Robot.limelightSubsystem.setCameraParams("pipeline", 0); // Switch to Cargo Pipeline
    }
}