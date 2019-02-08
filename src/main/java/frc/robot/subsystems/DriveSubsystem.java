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

    // deadzones by Alejandro at Chris' request
    private static final double DEADZONE_LEFT = 0.1;
    private static final double DEADZONE_RIGHT = 0.1;
    private static final double LEFT_EXPONENT = 1;
    private static final double RIGHT_EXPONENT = 1;
    private static final double MAX_JOYSTICK = 1;

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

    /**
     * Processes a given joystick axis value to match the given deadzone and shape as determined by the given exponent
     * @param x raw axis input
     * @param deadzone given deadzone in either direction
     * @param exponent exponent of the power output curve. 0 is linear. Higher values give you more precision in the
     *                 lower ranges, and lower values give you more precision in the higher ranges.
     *                 I, Alejandro, recommend values between -1-deadzone and 2.
     * @param maxOutput The maximum output this method returns. Set it to the max joystick output for best results.
     * @return the processed axis value. Send this to the motors.
     */
    private double axisProcessed(double x, double deadzone, double exponent, double maxOutput) {
        // positive input between deadzone and max
        if (deadzone < x && x < maxOutput) {
            return Math.pow(x, exponent) * ((x - deadzone) / (maxOutput - deadzone));
        // negative input between negative deadzone and negative max
        } else if (-maxOutput < x && x < -deadzone) {
            return -Math.pow(-x, exponent) * ((-x - deadzone) / (maxOutput - deadzone));
        // the input does not exceed the deadzone in either direction
        } else if (-deadzone < x && x < deadzone) {
            return 0;
        // somehow the input has exceeded the range of (-maxOutput, maxOutput)
        // this means that someone was playing with the code. Fix the maxOutput.
        } else {
            return x;
        }
    }

    public void setSpeed(Joystick leftStick, Joystick rightStick) {
        setSpeedTankAngularControl(-axisProcessed(leftStick.getY(), DEADZONE_LEFT, LEFT_EXPONENT, MAX_JOYSTICK),
                -axisProcessed(rightStick.getY(), DEADZONE_RIGHT, RIGHT_EXPONENT, MAX_JOYSTICK));
	}
	
	public void setSpeedRaw(Joystick leftStick, Joystick rightStick){
		setSpeedTank(-axisProcessed(leftStick.getY(), DEADZONE_LEFT, LEFT_EXPONENT, MAX_JOYSTICK),
                -axisProcessed(rightStick.getY(), DEADZONE_RIGHT, RIGHT_EXPONENT, MAX_JOYSTICK));
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
		setSpeedTankForwardManual(-axisProcessed(Robot.oi.leftStick.getY(), DEADZONE_LEFT, LEFT_EXPONENT, MAX_JOYSTICK),
                -axisProcessed(Robot.oi.rightStick.getY(), DEADZONE_RIGHT, RIGHT_EXPONENT, MAX_JOYSTICK), turnMagnitude);
	}

    @Override
    protected void initDefaultCommand() {
		//IGNORE THIS METHOD
    }
}