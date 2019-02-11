package frc.robot.subsystems;

import frc.robot.PortMap;
import frc.robot.Robot;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Subsystem;

public class DriveSubsystem extends Subsystem {
    // Define tested error values here
    double tankAngleP = 3;
	double goalOmegaConstant = 1;
	
    public CANSparkMax lf, lm, lb, rf, rm, rb;

    // deadzones by Alejandro at Chris' request
    private static final double DEADZONE = 0;
    private static final double EXPONENT = 0;
    private static final double MAX_JOYSTICK = 1;

    private CANSparkMax newMotorObject(int port){
        return new CANSparkMax(port,MotorType.kBrushless);
    }
	/**
     * Initialize robot's motors
     */
    public DriveSubsystem(){

		lf = newMotorObject(PortMap.LEFT_FRONT_SPARK);
		lm = newMotorObject(PortMap.LEFT_MIDDLE_SPARK);
        lb = newMotorObject(PortMap.LEFT_BACK_SPARK);
        
		rf = newMotorObject(PortMap.RIGHT_FRONT_SPARK);
		rm = newMotorObject(PortMap.RIGHT_MIDDLE_SPARK);
		rb = newMotorObject(PortMap.RIGHT_BACK_SPARK);
	}

    /**
     * Processes a given joystick axis value to match the given deadzone and shape as determined by the given exponent.
     *
     * The equations were made by Bowen.
     * @param x raw axis input
     * @return the processed axis value. Send this to the motors.
     */
    private double processAxis(double x) {
        // positive input between deadzone and max
        if (DEADZONE < x && x <= MAX_JOYSTICK) {
            return (MAX_JOYSTICK / axisFunction(x)) * axisFunction(MAX_JOYSTICK);

        // negative input between negative deadzone and negative max
        } else if (-MAX_JOYSTICK <= x && x < -DEADZONE) {
            return (-MAX_JOYSTICK / axisFunction(x)) * axisFunction(-MAX_JOYSTICK);

        // the input does not exceed the deadzone in either direction
        } else if (-DEADZONE <= x && x <= DEADZONE) {
            return 0;

        // somehow the input has exceeded the range of [-maxOutput, maxOutput]
        // this means that someone was playing with the code. Fix the maxOutput.
        } else {
            return x;
        }
    }

    /*
     * Used by processAxis as the main function of the curves.
     */
    private double axisFunction(double x) {
        return Math.pow(x - DEADZONE, EXPONENT) * ((x - DEADZONE) / (MAX_JOYSTICK - DEADZONE));
    }

    public void setSpeed(Joystick leftStick, Joystick rightStick) {
        double left = processAxis(leftStick.getY());
        double right = processAxis(rightStick.getY());
        System.out.println("Left: " + leftStick.getY() + " " + left + " Right: " + rightStick.getY() + " " + right);
        setSpeedTank(left, right);
    }
    
    public double processStick(Joystick stick){
        return processAxis(-stick.getY());
    }
	
	public void setSpeedRaw(Joystick leftStick, Joystick rightStick){
		setSpeedTank(processStick(leftStick),processStick(rightStick));
	}
        	
	public void setSpeedTank(double leftSpeed, double rightSpeed) {
		lf.set(-leftSpeed);
		lm.set(-leftSpeed);
		lb.set(-leftSpeed);

		rf.set(rightSpeed);
		rm.set(rightSpeed);
		rb.set(rightSpeed);
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
        setSpeedTankForwardManual(processStick(Robot.oi.leftStick),processStick(Robot.oi.rightStick),turnMagnitude);
	}

    @Override
    protected void initDefaultCommand() {
		//IGNORE THIS METHOD
    }
}