package frc.robot.helpers;

import frc.robot.Robot;

import java.util.ArrayList;
import java.util.Hashtable;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANSparkMax;

import frc.robot.PortMap;
import frc.robot.Utils;
import frc.robot.helpers.Pigeon;
import frc.robot.logging.Logger.DefaultValue;

public class RobotPosition {

    public static enum ChassisSide {Left, Right};

    public static final double WHEEL_RADIUS = Utils.inchesToMeters(3);
    public static final double ENC_WHEEL_RATIO_LOW_GEAR = 1 / 19.16; // 1 rotations of the wheel is 9.08 rotations of the encoder
    public static final double ENC_WHEEL_RATIO_HIGH_GEAR = 1 / 9.07;
    public static final double ROBOT_RADIUS = Utils.inchesToMeters(15.945); // Half the distance from wheel to wheel

    public static final double ORIGINAL_ANGLE = Math.PI/2, ORIGINAL_X = 0, ORIGINAL_Y = 0;
    public static final int WHEEL_MEASURMENTS = 5; // How many values we keep track of for each wheel
    public static final int ANGLE_MEASURMENTS = 5; // How many values we keep track of for our angle
    public static final double INTERVAL_LENGTH = .02; // Seconds between each tick for commands
    public static final double STATIC_POSITION_ERROR = .01 * WHEEL_MEASURMENTS; // If we have moved less than this, we say we aren't moving (in meters)
    public static final double STATIC_ANGLE_ERROR = Math.toRadians(.4) * ANGLE_MEASURMENTS; // If we have turned less than this, we say we aren't turning
	private final String fileName = "robotPosition.java";

    private ArrayList<Double> robotXs, robotYs, robotAngles;
    private Hashtable<CANSparkMax,ArrayList<Double>> wheelPositions;
    private Hashtable<CANSparkMax,Boolean> negated; // The motors on the left have negated inputs/outputs. This keeps track of that
    private ArrayList<CANSparkMax> sparks;
    private Pigeon pigeon;
    private TalonSRX pigeonTalon;

    public RobotPosition(){

        this.pigeonTalon = new TalonSRX(PortMap.PIGEON_TALON);
        this.pigeon      = new Pigeon(pigeonTalon);

        this.robotXs     = new ArrayList<Double>();
        this.robotYs     = new ArrayList<Double>();
        this.robotAngles = new ArrayList<Double>();

        this.wheelPositions = new Hashtable<CANSparkMax,ArrayList<Double>>();
        this.negated        = new Hashtable<CANSparkMax,Boolean>();
        this.sparks         = new ArrayList<CANSparkMax>();

        keepTrackOfWheel(Robot.driveSubsystem.lm,ChassisSide.Left);
        keepTrackOfWheel(Robot.driveSubsystem.rm,ChassisSide.Right);

        resetPosition();
    }
    
    public ArrayList<Double> getRobotXs(){return this.robotXs;}   
    public ArrayList<Double> getRobotYs(){return this.robotYs;}
    public ArrayList<Double> getAngles() {return this.robotAngles;}

    public void keepTrackOfWheel(CANSparkMax spark,ChassisSide chassisSide){
        // this allows the code to easily keep track of the position of motors for the chassis
        this.wheelPositions.put(spark,new ArrayList<Double>());
        this.negated.put(spark,chassisSide == ChassisSide.Left);
        this.sparks.add(spark);
    }

    public TalonSRX[] getTalons() {
        return new TalonSRX[]{this.pigeonTalon};
    }

    public Pigeon getPigeon(){return this.pigeon;}
    
    public void setPosition(double robotX, double robotY, double angle) {
        // Here we update all of our most recent measurements so we are saying we are at this position now rather than when we did the previous measurement
        for (CANSparkMax spark : sparks) {recordWheelPosition(spark);}
        pigeon.setAngle(angle);

    	trimAddWheelMeasurement(this.robotXs    ,robotX);
    	trimAddWheelMeasurement(this.robotYs    ,robotY);
        trimAddAngleMeasurement(this.robotAngles,angle);
    }
    
    public void resetPosition() {
        setPosition(ORIGINAL_X,ORIGINAL_Y,ORIGINAL_ANGLE);
    }   

    public double wheelPosition(CANSparkMax motor) {
        // Returns the encoder position of a spark
        double value = ENC_WHEEL_RATIO_LOW_GEAR * Robot.encoderSubsystem.getSparkAngle(motor) * WHEEL_RADIUS; // Should change to alternate low gear/high gear with whatever it is
        return this.negated.get(motor) ? (0 - value) : value;
    }

    public void recordWheelPosition(CANSparkMax spark){
        trimAddWheelMeasurement(this.wheelPositions.get(spark),wheelPosition(spark));
    }
    public double wheelRotationChange(CANSparkMax spark){
        double lastWheelPosition = lastWheelPosition(spark);
        recordWheelPosition(spark);
        return lastWheelPosition(spark) - lastWheelPosition;
    }

    private void trimAddWheelMeasurement(ArrayList<Double> arr, double val){
        // Uses ENC_MEASURMENTS as the maxSize, should work if you are adding to an array list of encoder measurmeants
        Utils.trimAdd(arr, val, WHEEL_MEASURMENTS);
    }
    private void trimAddAngleMeasurement(ArrayList<Double> arr, double val){
        // Uses ANGLE_MEASURMENTS as the maxSize, should work if you are adding to an array list of angle (pigeonIMU) measurmeants
        Utils.trimAdd(arr, val, ANGLE_MEASURMENTS);
    }

    public double getAngle(){return Utils.last(this.robotAngles);}
    public double getX()    {return Utils.last(this.robotXs);}
    public double getY()    {return Utils.last(this.robotYs);}

    public boolean xStatic()    {return Utils.inRange(this.robotXs    ,STATIC_POSITION_ERROR);}
    public boolean yStatic()    {return Utils.inRange(this.robotYs    ,STATIC_POSITION_ERROR);}
    public boolean angleStatic(){return Utils.inRange(this.robotAngles,STATIC_ANGLE_ERROR);}
    public boolean robotStatic(){
        return xStatic() && yStatic() && angleStatic();
    }
    
    public double getAngularVelocity(){return Utils.averageRange(this.robotAngles) / INTERVAL_LENGTH;}
    public double getXVelocity()      {return Utils.averageRange(this.robotXs)     / INTERVAL_LENGTH;}
    public double getYVelocity()      {return Utils.averageRange(this.robotYs)     / INTERVAL_LENGTH;}
    public double getSpeed(){
        return Math.sqrt(Math.pow(getXVelocity(), 2) + Math.pow(getYVelocity(), 2));
    }
    
    public double lastWheelPosition(CANSparkMax spark)  {
        // Takes a spark. Returns the last recorded pos of that spark/wheel
        return Utils.last(wheelPositions.get(spark));
    }

    public double getWheelSpeed(CANSparkMax spark) {
        // Gets average wheel speed over the recorded measurmeants
        return Utils.averageRange(wheelPositions.get(spark)) / INTERVAL_LENGTH;
    }

    public Hashtable<String,Double> newWheelChangeInfo(double rotationChange, double angle){
        return new Hashtable<String,Double>(){{
            put("rotationChange", rotationChange);
            put("angle", angle);
        }};
    }

    public double[] nextPosition(double x, double y, double theta, ArrayList<Hashtable<String,Double>> allChangeInfo){
        // Works for all forms of drive where the displacement is the average of the movement vectors over the wheels
        double newTheta = pigeon.getAngle();
        double avgTheta = (theta + newTheta)/2;
        int wheelAmount = allChangeInfo.size();
        for(Hashtable<String,Double> wheelChangeInfo : allChangeInfo){
            x += wheelChangeInfo.get("rotationChange") * Math.cos(avgTheta + wheelChangeInfo.get("angle")) / wheelAmount;
            y += wheelChangeInfo.get("rotationChange") * Math.sin(avgTheta + wheelChangeInfo.get("angle")) / wheelAmount;
        }
        return new double[]{x,y,newTheta};
    }
 
    public double[] nextPosTankPigeon(double x, double y, double theta, double leftChange, double rightChange) {
        // This assumes tank drive and you want to use the pigeon for calculating your angle
        // Takes a pos (x,y,theta), a left side Δx and a right side Δx and returns an x,y,theta array
        ArrayList<Hashtable<String,Double>> allChangeInfo = new ArrayList<Hashtable<String,Double>>();
        allChangeInfo.add(newWheelChangeInfo(leftChange,0));
        allChangeInfo.add(newWheelChangeInfo(rightChange, 0)); // the zeros represent that they aren't turned
        return nextPosition(x,y,theta,allChangeInfo);
    }

    public void changePoint(double[] point){
        setPosition(point[0],point[1],point[2]);
    }

    public void updatePositionTank(){
        //System.out.println("updating position");
        changePoint(nextPosTankPigeon(getX(), getY(), getAngle(), wheelRotationChange(Robot.driveSubsystem.lm), wheelRotationChange(Robot.driveSubsystem.rm))); 
    }
    
	public void periodicLog(){
        Robot.logger.addData(this.fileName, "robot X", getX(), DefaultValue.Previous);
        Robot.logger.addData(this.fileName, "robot y", getY(), DefaultValue.Previous);
        Robot.logger.addData(this.fileName, "robot angle", getAngle(), DefaultValue.Previous);
        Robot.logger.addData(this.fileName, "angular speed", getAngularVelocity(), DefaultValue.Previous);
	}

    public void printPosition(){
        System.out.println("X: " + getX());
        System.out.println("Y: " + getY());
        System.out.println("Angle: " + Math.toDegrees(getAngle()));
    }
}