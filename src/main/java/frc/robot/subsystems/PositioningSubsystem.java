package frc.robot.subsystems;

import java.util.ArrayList;
import java.util.Hashtable;

import frc.robot.Robot;
import frc.robot.Utils;

import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.command.Subsystem;

public class PositioningSubsystem extends Subsystem {

	public final double INCHES_PER_METER = 39.37;
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

    private ArrayList<Double> robotXs, robotYs, robotAngles;
    private Hashtable<CANSparkMax,ArrayList<Double>> encPoss;
    private Hashtable<CANSparkMax,Boolean> negated;
    private ArrayList<CANSparkMax> sparks;
    
    public ArrayList<Double> getRobotXs(){return robotXs;}   
    public ArrayList<Double> getRobotYs(){return robotYs;}
    public ArrayList<Double> getAngles() {
        ArrayList<Double> adjustedAngles = new ArrayList<Double>();
        for (Double angle : robotAngles){adjustedAngles.add(adjustTheta(angle));}
        return adjustedAngles;
    }

    public void keepTrackOf(CANSparkMax spark,Boolean neg){
        encPoss.put(spark,new ArrayList<Double>());
        negated.put(spark,neg);
        sparks.add(spark);
    }

    public PositioningSubsystem(){

        ORIGINAL_X = 0;
        ORIGINAL_Y = 0;
        ORIGINAL_ANGLE = Robot.getPigeonAngle();

        robotXs     = new ArrayList<Double>();
        robotYs     = new ArrayList<Double>();
        robotAngles = new ArrayList<Double>();

        encPoss = new Hashtable<CANSparkMax,ArrayList<Double>>();
        negated = new Hashtable<CANSparkMax,Boolean>();
        sparks = new ArrayList<CANSparkMax>();

        keepTrackOf(Robot.lm,true); // true and false indicates whether the values must be negated
        keepTrackOf(Robot.rm,false);

        resetPosition();
        
    }
    
    public void setPosition(double robotX, double robotY, double angle) {
    	trimAddDef(robotXs,robotX);
    	trimAddDef(robotYs,robotY);
    	trimAddDef(robotAngles,angle);
    }
    
    public void resetPosition() {
    	ORIGINAL_ANGLE = Robot.getPigeonAngle();
        setPosition(ORIGINAL_X,ORIGINAL_Y,ORIGINAL_ANGLE);
        for (CANSparkMax spark : sparks)
            addEncPos(spark);
    	}

    public double encPos(CANSparkMax motor) {
        // Returns the encoder position of a spark
        double rotations = motor.getEncoder().getPosition();
        //System.out.println(raw);
        double value = rotations * ENC_WHEEL_RATIO * (2 * Math.PI * WHEEL_RADIUS);
        return negated.get(motor) ? (0 - value) : value;
    }

    public void addEncPos(CANSparkMax spark){trimAddDef(encPoss.get(spark),encPos(spark));}
    public double encUpdate(CANSparkMax spark){
        // Also returns the change
        double lastPos = lastEncPos(spark);
        addEncPos(spark);
        return lastEncPos(spark) - lastPos;}

    private void trimAddDef(ArrayList<Double> arr, double val){
        // Uses MEASURMENTS as the maxSize, Def is short for default
        Utils.trimAdd(arr, val, MEASURMENTS);
    }

    public double adjustTheta(double theta) {return theta + GLOBAL_ORIGINAL_ANGLE - ORIGINAL_ANGLE;}
    public double getAngle() {return adjustTheta(Utils.last(robotAngles));}
    public double getX()     {return Utils.last(robotXs);}
    public double getY()     {return Utils.last(robotYs);}

    public boolean inRange(double n1, double n2, double error) {return Math.abs(n1 - n2) < error;}
    public boolean xStatic() {return inRange(getX(),robotXs.get(0),STATIC_POSITION_ERROR);}
    public boolean yStatic() {return inRange(getY(),robotYs.get(0),STATIC_POSITION_ERROR);}
    public boolean angleStatic() {return inRange(getAngle(),robotAngles.get(0),STATIC_ANGLE_ERROR);}
    public boolean robotStatic() {return xStatic() && yStatic() && angleStatic();}
    
    public double getAngularSpeed() {
    	return (getAngle() - adjustTheta(robotAngles.get(0))) / ((MEASURMENTS - 1) * INTERVAL_LENGTH);
    }
    
    public double lastEncPos(CANSparkMax spark)  {
        // Takes a spark. Returns the last recorded pos of that spark
        return Utils.last(encPoss.get(spark));
    }

    private double averageRange(ArrayList<Double> arr) {
        return (Utils.last(arr) - arr.get(0)) / arr.size();
    }

    public double getWheelSpeed(CANSparkMax spark) {
        // Gets average wheel speed over the recorded measurmeants
        return averageRange(encPoss.get(spark)) / INTERVAL_LENGTH;
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
 
    public double[] nextPosTankPigeon(double x, double y, double theta, double leftChange, double rightChange) {
        // This assumes tank drive and you want to use the pigeon for calculating your angle
        // Takes a pos (x,y,theta), a left side Δx and a right side Δx and returns an x,y,theta array
        double[][] changeAngles = new double[][]{{leftChange,0},{rightChange,0}}; // the zeros represent that htey aren't turned
        return nextPosPigeon(x,y,theta,changeAngles);
    }

    public void changePoint(double[] point){setPosition(point[0],point[1],point[2]);}

    public void updatePositionTank(){
        changePoint(nextPosTankPigeon(getX(), getY(), getAngle(), encUpdate(Robot.lm), encUpdate(Robot.rm))); 
    }

    public void printPosition(){
        System.out.println("X: " + getX());
        System.out.println("Y: " + getY());
        System.out.println("Angle: " + getAngle());
    }

    @Override
    protected void initDefaultCommand() {
        // LITTERALLY DIE
    }
}
