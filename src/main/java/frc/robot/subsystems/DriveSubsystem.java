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

    // deadzones by Alejandro at Chris' request. Graph them with the joystick function to understand the math.
    // https://www.desmos.com/calculator/ch19ahiwol
    private static final double INPUT_DEADZONE = 0.06; // deadzone because the joysticks are bad and they detect input when there is none
    private static final double MOTOR_MOVEPOINT = 0.1; // motor controller output that gets the wheels to turn
    private static final double EXPONENT = 10; // x^exponent to in the graph. x=0 is linear. x>0 gives more control in low inputs
    private static final double MAX_JOYSTICK = 1; // max joystick output value

    // d value so that when x=INPUT_DEADZONE the wheels move
    private static final double ALEJANDROS_CONSTANT = (MAX_JOYSTICK * Math.pow(MOTOR_MOVEPOINT / MAX_JOYSTICK, 1/(EXPONENT+1)) - INPUT_DEADZONE) /
                                                    (Math.pow(MOTOR_MOVEPOINT / MAX_JOYSTICK, 1/(EXPONENT+1)) - 1);

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
        double sign = Math.abs(x) / x;
        return sign * (MAX_JOYSTICK / axisFunction(MAX_JOYSTICK)) * axisFunction(Math.abs(x));
    }

    // Used by processAxis as the main function of the curves.
    private double axisFunction(double x) {
        double adjustedX = Math.max(x - ALEJANDROS_CONSTANT, 0);
        return Math.pow(adjustedX, EXPONENT) * ((adjustedX) / (MAX_JOYSTICK - ALEJANDROS_CONSTANT));
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
