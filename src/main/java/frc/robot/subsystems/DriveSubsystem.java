/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import frc.robot.Robot;

public class DriveSubsystem {
    private Talon frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor;

    /**
     * Initialize robot's motors
     */
    public DriveSubsystem() {
        // Temporary PWM channels
        frontLeftMotor = new Talon(1);
        backLeftMotor = new Talon(2);
        frontRightMotor = new Talon(3);
        backRightMotor = new Talon(4);
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
     * @param rightStick    The right joystick - used for lateral movements
     * @param leftStick     The left joystick - used for rotations
     */
    public void setSpeed(Joystick rightStick, Joystick leftStick, Modes mode) {
        double x = 0.0;
        double y = 0.0;
        
        if (mode == Modes.FIELD) {
            double angle = rightStick.getDirectionRadians() - getPigeonAngle();
            x = Math.cos(angle) * rightStick.getMagnitude();
            y = Math.sin(angle) * rightStick.getMagnitude();
        } else {
            x = rightStick.getX();
            y = rightStick.getY();
        }

        mecanumDrive(x, -y, leftStick.getX());
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
    public void mecanumDrive(double xSpeed, double ySpeed, double rotation) {
        frontLeftMotor.set(-xSpeed - ySpeed + rotation);
        backLeftMotor.set(xSpeed - ySpeed + rotation);
        frontRightMotor.set(-xSpeed + ySpeed + rotation);
        backRightMotor.set(xSpeed + ySpeed + rotation);
    }
}