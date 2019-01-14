/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package org.usfirst.frc.team1155.robot.subsystems;


import org.usfirst.frc.team1155.robot.PortMap;
import org.usfirst.frc.team1155.robot.Robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Subsystem;

public class DriveSubsystem extends Subsystem {
    private CANSparkMax leftFrontSpark, leftBackSpark, rightFrontSpark, rightBackSpark;

    /**
     * Initialize robot's motors
     */
    public DriveSubsystem() {
        // Temporary PWM channels
        leftFrontSpark = new CANSparkMax(PortMap.LEFT_FRONT_SPARK, MotorType.kBrushless);
        leftBackSpark = new CANSparkMax(PortMap.LEFT_BACK_SPARK, MotorType.kBrushless);
        rightFrontSpark = new CANSparkMax(PortMap.RIGHT_FRONT_SPARK, MotorType.kBrushless);
        rightBackSpark = new CANSparkMax(PortMap.RIGHT_BACK_SPARK, MotorType.kBrushless);
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
        leftFrontSpark.set(-xSpeed - ySpeed + rotation);
        leftBackSpark.set(xSpeed - ySpeed + rotation);
        rightFrontSpark.set(-xSpeed + ySpeed + rotation);
        rightBackSpark.set(xSpeed + ySpeed + rotation);
    }

    @Override
    protected void initDefaultCommand() {

    }
}