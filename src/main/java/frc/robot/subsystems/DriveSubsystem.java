/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.PortMap;
import frc.robot.Robot;
import frc.robot.PID;

public class DriveSubsystem extends Subsystem {
    private TalonSRX frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor;
    public TalonSRX talonWithPigeon;
    // Define tested error values here
    double kp, kd;
    PID pid = new PID(kp, 0, kd);
    /** 
     * Initialize robot's motors
     */
    public DriveSubsystem() {
        // Temporary PWM channels
        frontLeftMotor = new TalonSRX(PortMap.LEFT_FRONT_TALON);
        backLeftMotor = new TalonSRX(PortMap.LEFT_BACK_TALON);
        frontRightMotor = new TalonSRX(PortMap.RIGHT_FRONT_TALON);
        backRightMotor = new TalonSRX(PortMap.RIGHT_BACK_TALON);
        talonWithPigeon = frontLeftMotor;
        frontLeftMotor  = new TalonSRX(1);
        backLeftMotor   = new TalonSRX(2);
        frontRightMotor = new TalonSRX(3);
        backRightMotor  = new TalonSRX(4);
    }

    public double getPigeonAngle() {
        double[] yawPitchRoll = new double[3];
        Robot.pigeon.getYawPitchRoll(yawPitchRoll);
        return yawPitchRoll[0] % 360.;
    }

    public enum Modes {
        FIELD, ROBOT
    }

    /**
     * Feed joystick input to mecanumDrive.
     * 
     * @param rightStick The right joystick - used for lateral movements
     * @param leftStick  The left joystick - used for rotations
     */
    public void setSpeed(Joystick rightStick, Joystick leftStick, Modes mode) {
        double x, y = 0.0;

        if (mode == Modes.FIELD) {
            double angle = rightStick.getDirectionRadians() - Math.toRadians(getPigeonAngle());
            x = Math.cos(angle) * rightStick.getMagnitude();
            y = Math.sin(angle) * rightStick.getMagnitude();
        } else {
            x = rightStick.getX();
            y = rightStick.getY();
        }

        setSpeedMecanum(x, -y, leftStick.getX());
    }
    
    public void setTalon(TalonSRX talon, double speed) {
    	talon.set(ControlMode.PercentOutput, speed);
    }

    /**
     * Cartesian mecanum drive method.
     * 
     * Allows the robot to drive in any direction without changing its orientation.
     * The four wheels are arranged in such a manner that the front and back wheels
     * are "toed in" 45 degrees.
     * 
     * @param xSpeed   The speed that the robot should drive in the X direction
     * @param ySpeed   The speed that the robot should drive in the X direction
     * @param rotation The robot's rate of rotation
     */
    public void setSpeedMecanum(double xSpeed, double ySpeed, double rotation) {
        setTalon(frontLeftMotor,-xSpeed - ySpeed + rotation);
        setTalon(backLeftMotor,xSpeed - ySpeed + rotation);
        setTalon(frontRightMotor,-xSpeed + ySpeed + rotation);
        setTalon(backRightMotor,xSpeed + ySpeed + rotation);
    }
    
    public void turnDegreeMecanum(double rotationAngle){
        double error = getPigeonAngle() - rotationAngle;
        pid.add_measurement(error);
        setSpeedMecanum(0, 0, pid.getOutput());
    }
    
     
    @Override
    protected void initDefaultCommand() {

    }
}