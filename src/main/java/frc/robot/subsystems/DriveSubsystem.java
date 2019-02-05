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
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Subsystem;

public class DriveSubsystem extends Subsystem {
    // Define tested error values here
    double tankAngleP = 3;
	double goalOmegaConstant = 1;
	
    public CANSparkMax lf, lm, lb, rf, rm, rb;


	/** 
     * Initialize robot's motors
     */
    public DriveSubsystem(){

		lf = new CANSparkMax(PortMap.LEFT_FRONT_SPARK,MotorType.kBrushless);
		lm = new CANSparkMax(PortMap.LEFT_MIDDLE_SPARK,MotorType.kBrushless);
        lb = new CANSparkMax(PortMap.LEFT_BACK_SPARK,MotorType.kBrushless);
        
		rf = new CANSparkMax(PortMap.RIGHT_FRONT_SPARK,MotorType.kBrushless);
		rm = new CANSparkMax(PortMap.RIGHT_MIDDLE_SPARK,MotorType.kBrushless);
		rb = new CANSparkMax(PortMap.RIGHT_BACK_SPARK,MotorType.kBrushless);
	}

    public void setSpeed(Joystick leftStick, Joystick rightStick) {
        setSpeedTankAngularControl(-leftStick.getY(),-rightStick.getY());
	}
	
	public void setSpeedRaw(Joystick leftStick, Joystick rightStick){
		setSpeedTank(-leftStick.getY(),-rightStick.getY());
	}
        	
	public void setSpeedTank(double leftSpeed, double rightSpeed) {
		lf.set(leftSpeed);
		lm.set(leftSpeed);
		lb.set(leftSpeed);

		rf.set(-rightSpeed);
		rm.set(-rightSpeed);
		rb.set(-rightSpeed);
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
    
    public void setTurningPercentage(double turnMagnitude){
		setSpeedTankForwardManual(-Robot.oi.leftStick.getY(),-Robot.oi.rightStick.getY(),turnMagnitude);
	}
     
    @Override
    protected void initDefaultCommand() {
		//IGNORE THIS METHOD
    }
}