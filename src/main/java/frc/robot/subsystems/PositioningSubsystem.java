package main.java.frc.robot.subsystems;

import java.util.ArrayList;
import java.util.Hashtable;

import org.usfirst.frc.team1155.robot.Robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.PWMTalonSRX;
import edu.wpi.first.wpilibj.command.Subsystem;

public class PositioningSubsystem extends Subsystem {

    public final double TICKS_PER_ROTATION = 4096;
    public final double WHEEL_RADIUS = 3.; // (0.25) 3 inches over 12 inches is wheel radius in feet
    public final double ENC_WHEEL_RATIO = (4. / 25.) * (1. / 1.2); // (0.16) 4 rotations of the wheel is 25 rotations of
                                                                   // the encoder
    public final double ROBOT_RADIUS = 15.945;
    public final double ROBOT_WIDTH = 2 * ROBOT_RADIUS;

    public double ORIGINAL_ANGLE = 0;
    public final double ORIGINAL_X = 0;
    public final double ORIGINAL_Y = 0;
    public final double MEASURMENTS = 5;
    public final double INTERVAL_LENGTH = .02;

    private TalonSRX frontLeftMotor, middleLeftMotor, frontRightMotor, backLeftMotor, middleRightMotor, backRightMotor;

    private ArrayList<Double> robotXs, robotYs, robotAngles, leftEncoderPositions, rightEncoderPositions;
    private Hashtable<TalonSRX,ArrayList<Double>> encPoss;
    private double robotAngle;

    public PositioningSubsystem() {
        middleLeftMotor  = Robot.autoSubsystem.lm;
        middleRightMotor = Robot.autoSubsystem.rm;
        frontLeftMotor   = Robot.autoSubsystem.lf;
        frontRightMotor  = Robot.autoSubsystem.rf;
        backLeftMotor    = Robot.autoSubsystem.lb;
        backRightMotor   = Robot.autoSubsystem.rb;

        robotXs = new ArrayList<Double>();
        robotYs = new ArrayList<Double>();
        robotAngles = new ArrayList<Double>();

        encPoss = new Hashtable<TalonSRX,ArrayList<Double>>();

        encPoss.put(frontLeftMotor);
        encPoss.put(frontRightMotor);
        
        ORIGINAL_ANGLE = Robot.autoSubsystem.getPigeonAngle();
        robotAngle = ORIGINAL_ANGLE;

        robotXs.add(ORIGINAL_X);
        robotYs.add(ORIGINAL_Y);
        robotAngles.add(ORIGINAL_ANGLE);
        addNegEncPosition(frontLeftMotor);
        addEncPosition(frontRightMotor);
    }

    public double encPos(TalonSRX motor) {
        double raw = motor.getSensorCollection().getQuadraturePosition();
        return raw / TICKS_PER_ROTATION * ENC_WHEEL_RATIO * (2 * Math.PI * WHEEL_RADIUS);
    }
    public double negEncPos(TalonSRX motor){
        return 0 - encPos(motor);
    }

    private void addEncPosValue(TalonSRX talon, double val){
        encPoss.get(talon).add(val);
    }
    public void addEncPos(TalonSRX talon)   {addEncPosValue(talon,encPos(talon));}
    public void addNegEncPos(TalonSRX talon){addEncPosValue(talon,negEncPos(talon));}

    public double last(ArrayList<Double> arr) {
        return arr.get(arr.size() - 1);
    }

    public void trimIf(ArrayList<Double> arr, int maxSize) {
        while (maxSize < arr.size())
            arr.remove(0);
    }

    public void trimAdd(ArrayList<Double> arr, double val, int maxSize) {
        arr.add(val);
        trimIf(arr, maxSize);
    }

    public double getX() {return last(robotXs);}
    public double getY() {return last(robotXs);}
    public double getAngle() {return last(robotAngles);}

    public double lastEncPos(TalonSRX talon)  {
        return last(encPoss.get(talon));}

    private double averageRange(ArrayList<Double> arr) {
        return (last(arr) - arr.get(0)) / arr.size();}

    public double getWheelSpeed(TalonSRX talon) {
        return averageRange(encPoss.get(talon)) / INTERVAL_LENGTH;}

    public double[] nextPosTankPigeon(double x, double y, double theta, double leftChange, double rightChange) {
        double newTheta = Robot.autoSubsystem.getPigeonAngle();
        double averageTheta = (newAngle + theta)/2;
        double arcLength = .5 * (leftChange + rightChange);
        double newRobotX = x + arcLength * Math.cos(averageTheta);
        double newRobotY = y + arcLength * Math.sin(averageTheta);

        double[] newPoint = { newRobotX, newRobotY, newTheta };
        return newPoint;
    }

    public void updatePositionTank() {
        // This can be done more accuratly. This is an estimate we will fix
        double leftEncChange = negEncPos(frontLeftMotor) - lastEncPos(frontLeftMotor);
        double rightEncChange = encPos(frontRightMotor) - lastEncPos(frontRightMotor);
        double[] newPoint = nextPosTankPigeon(getX(), getY(), robotAngle, leftEncChange, rightEncChange);

        trimAdd(robotXs, newPoint[0], 5);
        trimAdd(robotYs, newPoint[1], 5);
        trimAdd(robotAngles, newPoint[2], 5);
        trimAdd(leftEncoderPositions, negEncPos(frontLeftMotor), 5);
        trimAdd(rightEncoderPositions, encPos(frontRightMotor), 5);
    }

    @Override
    protected void initDefaultCommand() {
        // LITTERALLY DIE
    }
}
