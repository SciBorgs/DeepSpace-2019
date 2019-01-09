package main.java.frc.robot.subsystems;

import java.util.ArrayList;
import java.util.Hashtable;

import org.usfirst.frc.team1155.robot.Robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import edu.wpi.first.wpilibj.PWMTalonSRX;
import edu.wpi.first.wpilibj.command.Subsystem;

public class PositioningSubsystem extends Subsystem {

    public final double TICKS_PER_ROTATION = 4096;
    public final double WHEEL_RADIUS = 3.; // In inches
    public final double ENC_WHEEL_RATIO = (4. / 25.) * (1. / 1.2); // 4 rotations of the wheel is 25 rotations of the encoder
    public final double ROBOT_RADIUS = 15.945; // Half the distance from wheel to wheel
    public final double ROBOT_WIDTH = 2 * ROBOT_RADIUS;

    public double ORIGINAL_ANGLE, ORIGINAL_X, ORIGINAL_Y;
    public final int MEASURMENTS = 5; // How many values we keep track of for each encoder
    public final double INTERVAL_LENGTH = .02; // Seconds between each tick for commands

    private TalonSRX frontLeftMotor, middleLeftMotor, frontRightMotor, backLeftMotor, middleRightMotor, backRightMotor;

    private ArrayList<Double> robotXs, robotYs, robotAngles;
    private Hashtable<TalonSRX,ArrayList<Double>> encPoss;
    private double robotAngle;

    public void keepTrackOf(TalonSRX talon){
        encPoss.put(talon,new ArrayList<Double>());
    }

    public PositioningSubsystem() {
        middleLeftMotor  = Robot.autoSubsystem.lm;
        middleRightMotor = Robot.autoSubsystem.rm;
        frontLeftMotor   = Robot.autoSubsystem.lf;
        frontRightMotor  = Robot.autoSubsystem.rf;
        backLeftMotor    = Robot.autoSubsystem.lb;
        backRightMotor   = Robot.autoSubsystem.rb;

        ORIGINAL_X = 0;
        ORIGINAL_Y = 0;
        ORIGINAL_ANGLE = Robot.autoSubsystem.getPigeonAngle();

        robotXs     = new ArrayList<Double>();
        robotYs     = new ArrayList<Double>();
        robotAngles = new ArrayList<Double>();

        encPoss = new Hashtable<TalonSRX,ArrayList<Double>>();

        keepTrackOf(frontLeftMotor);
        keepTrackOf(frontRightMotor);
        keepTrackOf(backLeftMotor);
        keepTrackOf(backRightMotor);

        robotXs.add(ORIGINAL_X);
        robotYs.add(ORIGINAL_Y);
        robotAngles.add(ORIGINAL_ANGLE);

        addNegEncPos(frontLeftMotor);
        addNegEncPos(backLeftMotor);
        addEncPos(frontRightMotor);
        addEncPos(backRightMotor);
    }

    public double encPos(TalonSRX motor) {
        // Returns the encoder position of a talon
        double raw = motor.getSensorCollection().getQuadraturePosition();
        return raw / TICKS_PER_ROTATION * ENC_WHEEL_RATIO * (2 * Math.PI * WHEEL_RADIUS);
    }
    public double negEncPos(TalonSRX motor){
        return 0 - encPos(motor);
    }

    private void addEncPosValue(TalonSRX talon, double val){
        // Adds a value to one of the lists recording talon data
        trimAddDef(encPoss.get(talon),val);
    }
    public void addEncPos(TalonSRX talon)   {addEncPosValue(talon,encPos(talon));}
    public void addNegEncPos(TalonSRX talon){addEncPosValue(talon,negEncPos(talon));}

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
    public void trimAddDef(ArrayList<Double> arr, double val){
        // Uses MEASURMENTS as the maxSize, Def is short for default
        trimAdd(arr, val, MEASURMENTS);
    }

    public double getX() {return last(robotXs);}
    public double getY() {return last(robotXs);}
    public double getAngle() {return last(robotAngles);}

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
        double newTheta = Robot.autoSubsystem.getPigeonAngle();
        double averageTheta = (newTheta + theta) / 2;
        for(double[] motorData : changeAngles){
            x += motorData[0] * Math.cos(averageTheta + motorData[1]) / motorData.length;
            y += motorData[0] * Math.sin(averageTheta + motorData[1]) / motorData.length;
        }
        return new double[]{x,y,newTheta};
    }

    public double[] nextPosMecanumPigeon(double x, double y, double theta, double flChange, double frChange, double blChange, double brChange){
        // Same as the one for Tank but for Mecanum
        double[][] changeAngles = new double[][]
                                {{flChange,theta - Angle},
                                {frChange,theta + Angle},
                                {blChange,theta + Angle},
                                {brChange,theta - Angle}};
        return nextPositionUniversalPigeon(x,y,theta,changeAngles);
    }
 
    public double[] nextPosTankPigeon(double x, double y, double theta, double leftChange, double rightChange) {
        // This assumes tank drive and you want to use the pigeon for calculating your angle
        // Takes a pos (x,y,theta), a left side Δx and a right side Δx and returns an x,y,theta array
        double[][] changeAngles = new double[][]{{leftChange,theta},{rightChange,theta}};
        return nextPosPigeon(x,y,theta,changeAngles);
    }

    public void updatePositionTank() {
        // Uses the front left and front right motor to update the position, assuming tank drive
        // Doesn't return anything, simply changes the fields that hold the position info
        double leftEncChange = negEncPos(frontLeftMotor) - lastEncPos(frontLeftMotor);
        double rightEncChange = encPos(frontRightMotor) - lastEncPos(frontRightMotor);
        double[] newPoint = nextPosTankPigeon(getX(), getY(), robotAngle, leftEncChange, rightEncChange);

        trimAddDef(robotXs, newPoint[0]);
        trimAddDef(robotYs, newPoint[1]);
        trimAddDef(robotAngles, newPoint[2]);
        addNegEncPos(frontLeftMotor);
        addEncPos(frontRightMotor);
    }

    @Override
    protected void initDefaultCommand() {
        // LITTERALLY DIE
    }
}
