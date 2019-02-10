package frc.robot.subsystems;

import frc.robot.PID;
import frc.robot.Robot;

import java.util.*;

import edu.wpi.first.wpilibj.command.Subsystem;

public class LineupSubsystem extends Subsystem {

	private double lineAngle; // To line up with
    private double desiredForward; // This is where we will aim for b/c of overshooting
    private double desiredShift;
    private PID shiftPID; // PID for deciding the difference in our wheels' speeds
    private PID forwardPID; // PID for deciding our speed
    private double forwardGoal; // This is where we actually want to end up
    private double forwardScale = 0.8; // We're assuming that it goes an extra 20 percent.
    private boolean retroFound;
    private boolean lidarFound;

    public LineupSubsystem() {}

    public void resetFound(){
        retroFound = false; 
        lidarFound = false;
    }
    
    public void resetInfo(double forwardChange, double shiftChange, double angleChange) {
    	lineAngle = Robot.positioningSubsystem.getAngle() + angleChange;
    	forwardGoal = forwardChange;
    	desiredForward = forwardScale * forwardGoal + parallelCoordinate();
        desiredShift = shiftChange + shiftCoordinate();
        shiftPID = new PID(.7,0,.13);
    	forwardPID = new PID(0.45,.02,0.05);
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
            resetInfo(getRetro("parallel"),getRetro("shift"),getRetro("rotation")); // maybe simply make rotation 0
        }
    }
    
    public PID getShiftPID(){
        return shiftPID;
    } 
    public PID getForwardPID(){
        return forwardPID;
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
        return Robot.retroreflectiveSubsystem.extractData();
    }
    public double getRetro(String key){
        return retroData().get(key);
    }
    public void updateRetroFound(){
        retroFound = Robot.retroreflectiveSubsystem.extractData().get("found") == 1;
    }
    public double getRotation() {
        double adjustBy = -Robot.lidarSubsystem.LIDAR_SHIFT;
        double lShift = getRetro("shiftL");
        double rShift = getRetro("shiftR");
        double lAngle = (int) Math.toDegrees(Math.atan(lShift + adjustBy));
        double rAngle = (int) Math.toDegrees(Math.atan(rShift + adjustBy));
        double leftD  = Robot.lidarSubsystem.angleDistance(lAngle);
        double rightD = Robot.lidarSubsystem.angleDistance(rAngle);
        updateRetroFound();
        lidarFound = leftD != 0 && rightD != 0;
        if (!lidarFound || !retroFound){
            return 0;
        }
        double lidarRotation = Robot.lidarSubsystem.wallRotation(360 + lAngle,rAngle);
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
        	shiftPID.add_measurement_d(shiftError(),Math.sin(deltaTheta())); // We use the sine of our change in angle as the derivative (that's the secret!)
	        forwardPID.add_measurement(parallelError());
	        double output =  shiftPID.getOutput();
	        double defaultSpeed = forwardPID.getOutput();
	        Robot.driveSubsystem.setSpeedTank(defaultSpeed * (1 + output), defaultSpeed * (1 - output)); // The output changes the percentage that goes to each side which makes it turn
        }
    	else if (retroFound) {
    		Robot.driveSubsystem.setSpeedTank(0, 0);
        }
        else {
            Robot.driveSubsystem.setSpeedRaw(Robot.oi.leftStick, Robot.oi.rightStick);
        }
    }
    

    @Override
    protected void initDefaultCommand() {
        // LITTERALLY DIE
    }
}
