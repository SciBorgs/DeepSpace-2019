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
import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Subsystem;

public class DriveSubsystem extends Subsystem {
    // Define tested error values here
    double tankAngleP = 3;
    double goalOmegaConstant = 1;
    PID ballFollowerPID;
    double ballFollowerP = 0.05;
    double ballFollowerI = 0;
    double ballFollowerD = 0.001;
    /** 
     * Initialize robot's motors
     */
    public DriveSubsystem() {
    	ballFollowerPID = new PID(ballFollowerP, ballFollowerI, ballFollowerD);
    }

    public void setSpeed(Joystick leftStick, Joystick rightStick) {
    	System.out.println("rs: " + rightStick.getY());
        setSpeedTankAngularControl(-leftStick.getY(),-rightStick.getY());
    }
        	
	public void setSpeedTank(double leftSpeed, double rightSpeed) {
		
		Robot.lf.set(-leftSpeed);
		Robot.lm.set(-leftSpeed);
		Robot.lb.set(-leftSpeed);

		Robot.rf.set(rightSpeed);
		Robot.rm.set(rightSpeed);
		Robot.rb.set(rightSpeed);
	}
	
	public void setSpeedTankAngularControl(double leftSpeed, double rightSpeed) {
		double averageOutput = (leftSpeed + rightSpeed) / 2;
		double goalOmega = goalOmegaConstant * (leftSpeed - rightSpeed);
		double change = (goalOmega - Robot.positioningSubsystem.getAngularSpeed()) * tankAngleP;
		setSpeedTank(averageOutput + change, averageOutput - change); 
	}
	
	public void setSpeedTankBallFollow(double leftSpeed, double rightSpeed, double tx) {
		ballFollowerPID.add_measurement(tx);
		double turnMagnitude = ballFollowerPID.getOutput();
		double avg = .5 * (leftSpeed + rightSpeed);
		setSpeedTank(avg + turnMagnitude, avg - turnMagnitude);
    }
    
    public void followBall(double tx){setSpeedTankBallFollow(-Robot.oi.leftStick.getY(),-Robot.oi.rightStick.getY(),tx);}
     
    @Override
    protected void initDefaultCommand() {

    }
}