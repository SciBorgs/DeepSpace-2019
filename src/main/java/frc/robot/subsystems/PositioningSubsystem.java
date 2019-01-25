package org.usfirst.frc.team1155.robot.subsystems;

import java.util.ArrayList;
import java.util.Hashtable;

import org.usfirst.frc.team1155.robot.Robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;

public class PositioningSubsystem extends Subsystem {

	public final double INCHES_PER_METER = 39.37;
    public final double TICKS_PER_ROTATION = 4096;
    public final double WHEEL_RADIUS = 3. / INCHES_PER_METER; // In meters
    public final double ENC_WHEEL_RATIO = (4. / 25.) * (1. / 1.2); // 4 rotations of the wheel is 25 rotations of the encoder
    public final double ROBOT_RADIUS = 15.945 / INCHES_PER_METER; // Half the distance from wheel to wheel
    public final double ROBOT_WIDTH = 2 * ROBOT_RADIUS;

    public final double GLOBAL_ORIGINAL_ANGLE = Math.PI/2;
    public double ORIGINAL_ANGLE, ORIGINAL_X, ORIGINAL_Y;
    public final int MEASURMENTS = 5; // How many values we keep track of for each encoder
    public final double INTERVAL_LENGTH = .02; // Seconds between each tick for commands
    public final double STATIC_POSITION_ERROR = .05;
    public final double STATIC_ANGLE_ERROR = Math.toRadians(2);

    private TalonSRX frontLeftMotor, frontRightMotor, middleLeftMotor, middleRightMotor, backLeftMotor, backRightMotor;

    private ArrayList<Double> robotXs, robotYs, robotAngles;
    private Hashtable<TalonSRX,ArrayList<Double>> encPoss;
    private Hashtable<TalonSRX,Boolean> negated;
    
    public ArrayList<Double> getRobotXs(){
    	return robotXs;
    }
    
    public ArrayList<Double> getRobotYs(){
    	return robotYs;
    }
    
    public ArrayList<Double> getAngles(){
    	return robotAngles;
    }

    public void keepTrackOf(TalonSRX talon,Boolean neg){
        encPoss.put(talon,new ArrayList<Double>());
        negated.put(talon,neg);
    }

    public PositioningSubsystem() {
        middleLeftMotor   = Robot.lm;
        middleRightMotor  = Robot.rm;

        ORIGINAL_X = 0;
        ORIGINAL_Y = 0;
        ORIGINAL_ANGLE = Robot.getPigeonAngle();

        robotXs     = new ArrayList<Double>();
        robotYs     = new ArrayList<Double>();
        robotAngles = new ArrayList<Double>();

        encPoss = new Hashtable<TalonSRX,ArrayList<Double>>();
        negated = new Hashtable<TalonSRX,Boolean>();

        keepTrackOf(middleLeftMotor,true); // true and false indicates whether the values must be negated
        keepTrackOf(middleRightMotor,false);

        resetPosition();
        
    }
    
    public void setPosition(double robotX, double robotY, double angle) {
    	trimAddDef(robotXs,robotX);
    	trimAddDef(robotYs,robotY);
    	trimAddDef(robotAngles,angle);
    }
    
    public void resetPosition() {
    	ORIGINAL_ANGLE = Robot.getPigeonAngle();
    	System.out.println("Original Angle: " + ORIGINAL_ANGLE);
    	setPosition(ORIGINAL_X,ORIGINAL_Y,ORIGINAL_ANGLE);
        addEncPos(middleLeftMotor);
        addEncPos(middleRightMotor);
    	}

    public double encPos(TalonSRX motor) {
        // Returns the encoder position of a talon
        double raw = motor.getSensorCollection().getQuadraturePosition();
        //System.out.println(raw);
        double value = raw / TICKS_PER_ROTATION * ENC_WHEEL_RATIO * (2 * Math.PI * WHEEL_RADIUS);
        return negated.get(motor) ? (0 - value) : value;
    }

    public void addEncPos(TalonSRX talon){trimAddDef(encPoss.get(talon),encPos(talon));}
    public double encUpdate(TalonSRX talon){
        // Also returns the change
        double lastPos = lastEncPos(talon);
        addEncPos(talon);
        return lastEncPos(talon) - lastPos;}

    public double last(ArrayList<Double> arr) {
        return arr.get(arr.size() - 1);
    }

    public void trimIf(ArrayList<Double> arr, int maxSize) {
        // Trims an array down to a max size, starting from the start
        while (maxSize < arr.size())
            arr.remove(0);
    }

    public void trimAdd(ArrayList<Double> arr, double val, int maxSize) {
        // Adds a value to the array and then trims it to a max size
        arr.add(val);
        trimIf(arr, maxSize);
    }
    private void trimAddDef(ArrayList<Double> arr, double val){
        // Uses MEASURMENTS as the maxSize, Def is short for default
        trimAdd(arr, val, MEASURMENTS);
    }

    public double getX() {return last(robotXs);}
    public double getY() {return last(robotYs);}
    public double adjustTheta(double theta) {return theta + GLOBAL_ORIGINAL_ANGLE - ORIGINAL_ANGLE;}
    public double getAngle() {return adjustTheta(last(robotAngles));}

    public boolean inRange(double n1, double n2, double error) {return Math.abs(n1 - n2) < error;}
    public boolean xStatic() {return inRange(getX(),robotXs.get(0),STATIC_POSITION_ERROR);}
    public boolean yStatic() {return inRange(getY(),robotYs.get(0),STATIC_POSITION_ERROR);}
    public boolean angleStatic() {return inRange(getAngle(),robotAngles.get(0),STATIC_ANGLE_ERROR);}
    public boolean robotStatic() {return xStatic() && yStatic() && angleStatic();}
    
    public double lastEncPos(TalonSRX talon)  {
        // Takes a talon. Returns the last recorded pos of that talon
        return last(encPoss.get(talon));
    }

    private double averageRange(ArrayList<Double> arr) {
        return (last(arr) - arr.get(0)) / arr.size();
    }

    public double getWheelSpeed(TalonSRX talon) {
        // Gets average wheel speed over the recorded measurmeants
        return averageRange(encPoss.get(talon)) / INTERVAL_LENGTH;
    }

    public double[] nextPosPigeon(double x, double y, double theta, double[][] changeAngles){
        // Works for all forms of drive where the displacement is the average of the movement vectors over the wheels
        double newTheta = Robot.getPigeonAngle();
        double avgTheta = (theta + adjustTheta(newTheta))/2;
        for(double[] motorData : changeAngles){
            x += motorData[0] * Math.cos(avgTheta + motorData[1]) / changeAngles.length;
            y += motorData[0] * Math.sin(avgTheta + motorData[1]) / changeAngles.length;
        }
        return new double[]{x,y,newTheta};
    }

    public double[] nextPosMecanumPigeon(double x, double y, double theta, double flChange, double frChange, double blChange, double brChange){
        // Same as the one for Tank but for Mecanum, probably gives bad estimates
        double Angle = Math.PI/2;
    	double[][] changeAngles = new double[][]
                                {{flChange,0 - Angle},
                                {frChange,Angle},
                                {blChange,Angle},
                                {brChange,0 - Angle}};
        return nextPosPigeon(x,y,theta,changeAngles);
    }
 
    public double[] nextPosTankPigeon(double x, double y, double theta, double leftChange, double rightChange) {
        // This assumes tank drive and you want to use the pigeon for calculating your angle
        // Takes a pos (x,y,theta), a left side Δx and a right side Δx and returns an x,y,theta array
        double[][] changeAngles = new double[][]{{leftChange,0},{rightChange,0}}; // the zeros represent that htey aren't turned
        return nextPosPigeon(x,y,theta,changeAngles);
    }

    public void changePoint(double[] point){setPosition(point[0],point[1],point[2]);}

    public void updatePositionMecanum(){
        changePoint(nextPosMecanumPigeon(getX(),getY(),getAngle(),
                encUpdate(frontLeftMotor),encUpdate(frontRightMotor),encUpdate(backLeftMotor),encUpdate(backRightMotor)));
    }

    public void updatePositionTank(){
        // Uses the front left and front right motor to update the position, assuming tank drive
        // Doesn't return anything, simply changes the fields that hold the position info
        changePoint(nextPosTankPigeon(getX(), getY(), getAngle(), encUpdate(middleLeftMotor), encUpdate(middleRightMotor)));
       // System.out.println("X: " + getX());
       // System.out.println("Y: " + getY());
       // System.out.println("Angle: " + getAngle());
    }

    @Override
    protected void initDefaultCommand() {
        // LITTERALLY DIE
    }
}
