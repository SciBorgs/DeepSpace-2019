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
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Subsystem;

public class DriveSubsystem extends Subsystem {
    // Define tested error values here
    double tankAngleP = 3;
    double goalOmegaConstant = 1;
	/** 
     * Initialize robot's motors
     */
    public DriveSubsystem(){}

    public void setSpeed(Joystick leftStick, Joystick rightStick) {
        setSpeedTankAngularControl(-leftStick.getY(),-rightStick.getY());
	}
	
	public void setSpeedRaw(Joystick leftStick, Joystick rightStick){
		setSpeedTank(-leftStick.getY(),-rightStick.getY());
	}
        	
	public void setSpeedTank(double leftSpeed, double rightSpeed) {
		System.out.println("right speed: " + rightSpeed);
	//	if ((leftSpeed + rightSpeed)/2. == )
		Robot.lf.set(leftSpeed);
		Robot.lm.set(leftSpeed);
		Robot.lb.set(leftSpeed);

		Robot.rf.set(-rightSpeed);
		Robot.rm.set(-rightSpeed);
		Robot.rb.set(-rightSpeed);
	}
	
	public void setSpeedTankAngularControl(double leftSpeed, double rightSpeed) {
		double averageOutput = (leftSpeed + rightSpeed) / 2;
		double goalOmega = goalOmegaConstant * (leftSpeed - rightSpeed);
		double change = (goalOmega - Robot.positioningSubsystem.getAngularSpeed()) * tankAngleP;
		setSpeedTank(averageOutput + change, averageOutput - change); 
	}
	
	public void setSpeedTankForwardManual(double leftSpeed, double rightSpeed, double turnMagnitude) {
		double avg = .5 * (leftSpeed + rightSpeed);
		setSpeedTank(avg * (1 + turnMagnitude), avg * (1 - turnMagnitude));
    }
    
    public void setTurningPercentage(double turnMagnitude){setSpeedTankForwardManual(-Robot.oi.leftStick.getY(),-Robot.oi.rightStick.getY(),turnMagnitude);}
     
    @Override
    protected void initDefaultCommand() {

    }
}