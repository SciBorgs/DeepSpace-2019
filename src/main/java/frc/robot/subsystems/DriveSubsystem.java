/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team1155.robot.subsystems;

import org.usfirst.frc.team1155.robot.PID;
import org.usfirst.frc.team1155.robot.PortMap;
import org.usfirst.frc.team1155.robot.Robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Subsystem;

public class DriveSubsystem extends Subsystem {
    // Define tested error values here
    double kp, kd;
    PID mecanumAnglePID = new PID(kp, 0, kd);
    PID tankAnglePID;
    /** 
     * Initialize robot's motors
     */
    public DriveSubsystem() {
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
            double angle = rightStick.getDirectionRadians() - Math.toRadians(Robot.getPigeonAngle());
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
        	
	public void setSpeedTank(double leftSpeed, double rightSpeed) {
		
		setTalon(Robot.lf, -leftSpeed);
		setTalon(Robot.lm, -leftSpeed);
		setTalon(Robot.lb, -leftSpeed);

		setTalon(Robot.rf, rightSpeed);
		setTalon(Robot.rm, rightSpeed);
		setTalon(Robot.rb, rightSpeed);
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
	
	public double roundControl(double speed) {
		return Math.round(speed * 10) / 10.0; 
	}
	
    public void setSpeedMecanum(double xSpeed, double ySpeed, double rotation) {
        setTalon(Robot.lf,-xSpeed - ySpeed + rotation);
        setTalon(Robot.lb,xSpeed - ySpeed + rotation);
        setTalon(Robot.rf,-xSpeed + ySpeed + rotation);
        setTalon(Robot.rb,xSpeed + ySpeed + rotation);
    }
    
    public void turnDegreeMecanum(double rotationAngle){
        double error = Robot.getPigeonAngle() - rotationAngle;
        mecanumAnglePID.add_measurement(error);
        setSpeedMecanum(0, 0, mecanumAnglePID.getOutput());
    }
    
    public void resetTurnPID() {resetTurnPID(.8, 2, 0.3);}
    public void resetTurnPID(double p, double i, double d) {tankAnglePID = new PID(p,i,d);}
    
    public void turnToDegreeTank(double desiredAngle) {
    	double error = desiredAngle - Robot.pos.getAngle();
    	System.out.println("error: " + error);
    	tankAnglePID.add_measurement(error);
    	double control = tankAnglePID.getOutput();
    	System.out.println("Control: " + control);
    	setSpeedTank(-control, control);
    }
    
     
    @Override
    protected void initDefaultCommand() {

    }
}