package frc.robot.helpers;

import frc.robot.Robot;

import java.util.*;


public class Lineup {

	private double lineAngle; // To line up with
    private double desiredForward; // This is where we will aim for b/c of overshooting
    private double desiredShift;
    private PID shiftPID; // PID for deciding the difference in our wheels' speeds
    private double forwardGoal; // This is where we actually want to end up
    private double forwardScale = 0.8; // We're assuming that it goes an extra 20 percent.
    private boolean retroFound;
    private boolean lidarFound;

    public Lineup() {}

    public void resetFound(){
        retroFound = false; 
        lidarFound = false;
    }
    
    public void resetInfo(double forwardChange, double shiftChange, double angleChange) {
    	lineAngle = Robot.positioningSubsystem.getAngle() + angleChange;
    	desiredForward = forwardScale * forwardChange + parallelCoordinate();
        desiredShift = shiftChange + shiftCoordinate();
        shiftPID = new PID(.7,0,.13);
    	shiftPID.setSmoother(6);
        }
        
    public void autoResetInfo(){
        if (retroFound && lidarFound){
            return;
        }
        double angleChange = getRotation();
        if (!retroFound){
            return;
        }
        double shiftChange = getShift(angleChange);
        double parallelChange = getParallel(angleChange);
        System.out.println("angle change: " + angleChange);
        System.out.println("parallel change: " + parallelChange);
        System.out.println("shift change: " + shiftChange);
        resetInfo(parallelChange,shiftChange,angleChange);
    }

    public void simpleResetInfo(){
        if (retroFound){
            return;
        }
        updateRetroFound();
        if (retroFound){
            resetInfo(getRetro("parallel"),getRetro("shift"),0); // maybe simply make rotation 0
        }
    }
    
    public PID getShiftPID(){
        return shiftPID;
    } 
    
    public double    shift(double x, double y, double angle) {
        return x * Math.sin(angle) - y * Math.cos(angle);
    }
    public double parallel(double x, double y, double angle) {
        return x * Math.cos(angle) + y * Math.sin(angle);
    }
    public double shiftCoordinate(){
        return shift(Robot.positioningSubsystem.getX(),Robot.positioningSubsystem.getY(),lineAngle);
    } // turns the line we are lining up with into the y-axis
    public double parallelCoordinate(){
        return parallel(Robot.positioningSubsystem.getX(),Robot.positioningSubsystem.getY(),lineAngle);
    }
    
    public double shiftError(){
        return desiredShift - shiftCoordinate();
    }
    public double parallelError(){
        return desiredForward - parallelCoordinate();
    }
    public double deltaTheta(){
        return Robot.positioningSubsystem.getAngle() - lineAngle;
    }

    public Hashtable<String,Double> retroData(){
        return RetroreflectiveDetection.extractData();
    }
    public double getRetro(String key){
        return retroData().get(key);
    }
    public void updateRetroFound(){
        retroFound = RetroreflectiveDetection.extractData().get("found") == 1;
    }
    public double getRotation() {
        double adjustBy = -LidarProcessing.LIDAR_SHIFT;
        double lShift = getRetro("shiftL");
        double rShift = getRetro("shiftR");
        double lAngle = (int) Math.toDegrees(Math.atan(lShift + adjustBy));
        double rAngle = (int) Math.toDegrees(Math.atan(rShift + adjustBy));
        double leftD  = LidarProcessing.angleDistance(lAngle);
        double rightD = LidarProcessing.angleDistance(rAngle);
        updateRetroFound();
        lidarFound = leftD != 0 && rightD != 0;
        if (!lidarFound || !retroFound){
            return 0;
        }
        double lidarRotation = LidarProcessing.wallRotation(360 + lAngle,rAngle);
        return Math.PI/2 - lidarRotation;
    }

    public double getShift(double deltaTheta){
        double shift = getRetro("shift");
        double parallel = getRetro("parallel");
        return shift(shift,parallel,deltaTheta);
    }
    public double getParallel(double deltaTheta){
        double shift = getRetro("shift");
        double parallel = getRetro("parallel");
        return parallel(shift,parallel,deltaTheta);
    }
    
    public void move(){
    	if (retroFound && parallelCoordinate() < desiredForward) {
        	shiftPID.add_measurement_dd(shiftError(),Math.sin(deltaTheta())); // We use the sine of our change in angle as the derivative (that's the secret!)
	        double output =  shiftPID.getOutput();
	        Robot.driveSubsystem.setTurningPercentage(output); // The output changes the percentage that goes to each side which makes it turn
        }
    	else if (retroFound) {
    		Robot.driveSubsystem.setSpeedTank(0, 0);
        }
        else {
            Robot.driveSubsystem.setSpeed(Robot.oi.leftStick, Robot.oi.rightStick);
        }
    }
}
