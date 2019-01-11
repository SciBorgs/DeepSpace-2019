/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Joystick;
<<<<<<< HEAD
import frc.robot.PortMap;
import frc.robot.Robot;

public class DriveSubsystem {
    private TalonSRX frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor;
	public TalonSRX talonWithPigeon;
=======
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.Robot;

public class DriveSubsystem extends Subsystem {
    private Talon frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor;
>>>>>>> 7ec28d974757f0c3a02e261dc6783b6de9736d91

    /**
     * Initialize robot's motors
     */
    public DriveSubsystem() {
        // Temporary PWM channels
<<<<<<< HEAD
        frontLeftMotor = new TalonSRX(PortMap.LEFT_FRONT_TALON);
        backLeftMotor = new TalonSRX(PortMap.LEFT_BACK_TALON);
        frontRightMotor = new TalonSRX(PortMap.RIGHT_FRONT_TALON);
        backRightMotor = new TalonSRX(PortMap.RIGHT_BACK_TALON);
        talonWithPigeon = frontLeftMotor;
=======
        frontLeftMotor  = new Talon(1);
        backLeftMotor   = new Talon(2);
        frontRightMotor = new Talon(3);
        backRightMotor  = new Talon(4);
>>>>>>> 7ec28d974757f0c3a02e261dc6783b6de9736d91
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

    @Override
    protected void initDefaultCommand() {

    }
}