package frc.robot.subsystems;

import java.util.ArrayList;
import java.util.Hashtable;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.PortMap;
import frc.robot.Robot;
import frc.robot.Utils;
import frc.robot.helpers.Pigeon;

public class PositioningSubsystem extends Subsystem {

    public static final double INCHES_PER_METER = 39.37;
    public static final double WHEEL_RADIUS = 3. / INCHES_PER_METER; // In meters
    public static final double ENC_WHEEL_RATIO_LOW_GEAR = 1 / 9.08; // 1 rotations of the wheel is 9.08 rotations of
                                                                          // the encoder
    public static final double ENC_WHEEL_RATIO_HIGH_GEAR = 1 / 9.08; // Find correct value
    public static final double NEO_TICKS_PER_REV = 1; // For sparks
    public static final double ENC_TICKS_PER_REV = 4096; // For talons
    public static final double ROBOT_RADIUS = 15.945 / INCHES_PER_METER; // Half the distance from wheel to wheel
    public static final double ROBOT_WIDTH = 2 * ROBOT_RADIUS;

    public static final double GLOBAL_ORIGINAL_ANGLE = Math.PI/2;
    public double ORIGINAL_ANGLE, ORIGINAL_X, ORIGINAL_Y;
    public static final int ENC_MEASURMENTS = 5; // How many values we keep track of for each encoder
    public static final int ANGLE_MEASURMENTS = 5;
    public static final double INTERVAL_LENGTH = .02; // Seconds between each tick for commands
    public static final double STATIC_POSITION_ERROR = .05;
    public static final double STATIC_ANGLE_ERROR = Math.toRadians(2);

    private ArrayList<Double> robotXs, robotYs, robotAngles;
    private Hashtable<CANSparkMax,ArrayList<Double>> encPoss;
    private Hashtable<CANSparkMax,Boolean> negated;
    private ArrayList<CANSparkMax> sparks;
    private Pigeon pigeon;
    private TalonSRX pigeonTalon;
    
    public ArrayList<Double> getRobotXs(){
        return robotXs;
    }   
    public ArrayList<Double> getRobotYs(){
        return robotYs;
    }
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

        pigeonTalon = new TalonSRX(PortMap.PIGEON_TALON);
        pigeon = new Pigeon(pigeonTalon);

        ORIGINAL_X = 0;
        ORIGINAL_Y = 0;
        ORIGINAL_ANGLE = pigeon.getAngle();

        robotXs     = new ArrayList<Double>();
        robotYs     = new ArrayList<Double>();
        robotAngles = new ArrayList<Double>();

        encPoss = new Hashtable<CANSparkMax,ArrayList<Double>>();
        negated = new Hashtable<CANSparkMax,Boolean>();
        sparks = new ArrayList<CANSparkMax>();

        keepTrackOf(Robot.driveSubsystem.lm,true); // true and false indicates whether the values must be negated
        keepTrackOf(Robot.driveSubsystem.rm,false);

        resetPosition();
        
    }

    public Pigeon getPigeon(){
        return pigeon;
    }
    
    public void setPosition(double robotX, double robotY, double angle) {
    	trimAddEnc(robotXs,robotX);
    	trimAddEnc(robotYs,robotY);
    	trimAddAngle(robotAngles,angle);
    }
    
    public void resetPosition() {
    	ORIGINAL_ANGLE = pigeon.getAngle();
        setPosition(ORIGINAL_X,ORIGINAL_Y,ORIGINAL_ANGLE);
        for (CANSparkMax spark : sparks)
            addEncPos(spark);
        }

    public double getTalonAngle(TalonSRX talon){
        return talon.getSensorCollection().getQuadraturePosition() / ENC_TICKS_PER_REV * 2 * Math.PI;
    }
	public double getSparkAngle(CANSparkMax spark){
		return spark.getEncoder().getPosition() / NEO_TICKS_PER_REV * 2 * Math.PI;
	}

    public double encPos(CANSparkMax motor) {
        // Returns the encoder position of a spark
        double wheel_ratio = Robot.gearShiftSubsystem.currentlyInHighGear() ? ENC_WHEEL_RATIO_HIGH_GEAR : ENC_WHEEL_RATIO_LOW_GEAR;
        double value = wheel_ratio * getSparkAngle(motor) * WHEEL_RADIUS;
        return negated.get(motor) ? (0 - value) : value;
    }

    public void addEncPos(CANSparkMax spark){trimAddEnc(encPoss.get(spark),encPos(spark));}
    public double encUpdate(CANSparkMax spark){
        // Also returns the change
        double lastPos = lastEncPos(spark);
        addEncPos(spark);
        return lastEncPos(spark) - lastPos;}

    private void trimAddEnc(ArrayList<Double> arr, double val){
        // Uses ENC_MEASURMENTS as the maxSize, should work if you are adding to an array list of encoder measurmeants
        Utils.trimAdd(arr, val, ENC_MEASURMENTS);
    }
    private void trimAddAngle(ArrayList<Double> arr, double val){
        // Uses ANGLE_MEASURMENTS as the maxSize, should work if you are adding to an array list of angle (pigeonIMU) measurmeants
        Utils.trimAdd(arr, val, ANGLE_MEASURMENTS);
    }

    public double adjustTheta(double theta){
        return theta + GLOBAL_ORIGINAL_ANGLE - ORIGINAL_ANGLE;
    }
    public double getAngle(){
        return adjustTheta(Utils.last(robotAngles));
    }
    public double getX(){
        return Utils.last(robotXs);
    }
    public double getY(){
        return Utils.last(robotYs);
    }

    public boolean inRange(double n1, double n2, double error){
        return Math.abs(n1 - n2) < error;
    }
    public boolean xStatic(){
        return inRange(getX(),robotXs.get(0),STATIC_POSITION_ERROR);
    }
    public boolean yStatic(){
        return inRange(getY(),robotYs.get(0),STATIC_POSITION_ERROR);
    }
    public boolean angleStatic(){
        return inRange(getAngle(),robotAngles.get(0),STATIC_ANGLE_ERROR);
    }
    public boolean robotStatic(){
        return xStatic() && yStatic() && angleStatic();
    }
    
    public double getAngularSpeed() {
        //System.out.println("current angle: " + getAngle());
        //System.out.println("last angle: " + adjustTheta(robotAngles.get(0)));
    	return (getAngle() - adjustTheta(robotAngles.get(0))) / ((ANGLE_MEASURMENTS - 1) * INTERVAL_LENGTH);
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
        double newTheta = pigeon.getAngle();
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

    public void changePoint(double[] point){
        setPosition(point[0],point[1],point[2]);
    }

    public void updatePositionTank(){
        changePoint(nextPosTankPigeon(getX(), getY(), getAngle(), encUpdate(Robot.driveSubsystem.lm), encUpdate(Robot.driveSubsystem.rm))); 
    }

    public void printPosition(){
        System.out.println("X: " + getX());
        System.out.println("Y: " + getY());
        System.out.println("Angle: " + Math.toDegrees(getAngle()));
    }

    @Override
    protected void initDefaultCommand() {
        // LITTERALLY DIE
    }
}
