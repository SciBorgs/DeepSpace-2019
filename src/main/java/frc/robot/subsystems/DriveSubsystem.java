/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import frc.robot.PID;
import frc.robot.PortMap;
import frc.robot.Robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Subsystem;

public class DriveSubsystem extends Subsystem {
    // Define tested error values here
    double kp, kd;
    PID mecanumAnglePID = new PID(kp, 0, kd);
    double tankAngleP = 3;
    double goalOmegaConstant = 1;
    PID ballFollowerPID;
    double ballFollowerP = 0.1;
    double ballFollowerI = 0;
    double ballFollowerD = 0.05;
    /** 
     * Initialize robot's motors
     */
    public DriveSubsystem() {
    	ballFollowerPID = new PID(ballFollowerP, ballFollowerI, ballFollowerD);
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
    public void setSpeed(Joystick leftStick, Joystick rightStick) {
    	System.out.println("rs: " + rightStick.getY());
        setSpeedTankAngularControl(-leftStick.getY(),-rightStick.getY());
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
	
	public void setSpeedTankAngularControl(double leftSpeed, double rightSpeed) {
		double averageOutput = (leftSpeed + rightSpeed) / 2;
		double goalOmega = goalOmegaConstant * (leftSpeed - rightSpeed);
		double change = (goalOmega - Robot.pos.getAngularSpeed()) * tankAngleP;
		setSpeedTank(averageOutput + change, averageOutput - change); 
	}
	
	public void setSpeedTankBallFollow(double leftSpeed, double rightSpeed, double tx) {
		ballFollowerPID.add_measurement(tx);
		double turnMagnitude = ballFollowerPID.getOutput();
		double avg = .5 * (leftSpeed + rightSpeed);
		setSpeedTank(avg - turnMagnitude, avg + turnMagnitude);
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
    
     
    @Override
    protected void initDefaultCommand() {

    }
}