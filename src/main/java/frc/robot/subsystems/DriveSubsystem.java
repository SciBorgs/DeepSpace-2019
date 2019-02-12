package frc.robot.subsystems;

import frc.robot.PortMap;
import frc.robot.Robot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class DriveSubsystem extends Subsystem {
    // Define tested error values here
    double tankAngleP = 3;
	double goalOmegaConstant = 1;
    private TalonSRX lf, lm, lb, rf, rm, rb;

    // deadzones by Alejandro at Chris' request
    private static final double INPUT_DEADZONE = 0.06; // deadzone because the joysticks are bad and they detect input when there is none
    private static final double OUTPUT_DEADZONE = -0.37473; // d value so that when x=INPUT_DEADZONE the wheels move
    private static final double EXPONENT = 1; // x^exponent to in the graph. x=0 is linear. x>0 gives more control in low inputs
    private static final double MAX_JOYSTICK = 1; // max joystick output value

    private TalonSRX newMotorObject(int port){
        return new TalonSRX(port);
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
        // filter input with its deadzone
        if (Math.abs(x) < INPUT_DEADZONE) {
            return 0;

        // positive input between deadzone and max
        } else if (INPUT_DEADZONE < x && x <= MAX_JOYSTICK) {
            return (MAX_JOYSTICK / axisFunction(MAX_JOYSTICK)) * axisFunction(x);

        // negative input between negative deadzone and negative max
        } else if (-MAX_JOYSTICK <= x && x < -INPUT_DEADZONE) {
            return (-MAX_JOYSTICK / axisFunction(MAX_JOYSTICK)) * axisFunction(-x);

        // somehow the input has exceeded the range of [-MAX_JOYSTICK, MAX_JOYSTICK]
        // this means that someone was playing with the code. Fix the MAX_JOYSTICK.
        } else {
            return x;
        }
    }

    /*
     * Used by processAxis as the main function of the curves.
     */
    private double axisFunction(double x) {
        return Math.pow(x - OUTPUT_DEADZONE, EXPONENT) * ((x - OUTPUT_DEADZONE) / (MAX_JOYSTICK - OUTPUT_DEADZONE));
    }

    public void setSpeed(Joystick leftStick, Joystick rightStick) {
        double left = -processAxis(leftStick.getY());
        double right = -processAxis(rightStick.getY());
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
		lf.set(ControlMode.PercentOutput, -leftSpeed);
		lm.set(ControlMode.PercentOutput, -leftSpeed);
		lb.set(ControlMode.PercentOutput, -leftSpeed);

		rf.set(ControlMode.PercentOutput, rightSpeed);
		rm.set(ControlMode.PercentOutput, rightSpeed);
		rb.set(ControlMode.PercentOutput, rightSpeed);
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
