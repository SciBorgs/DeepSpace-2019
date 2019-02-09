package frc.robot.subsystems;

import frc.robot.PortMap;
import frc.robot.Robot;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Subsystem;

public class DriveSubsystem extends Subsystem {
    // Define tested error values here
    double tankAngleP = 3;
	double goalOmegaConstant = 1;
	
    public TalonSRX lf, lm, lb, rf, rm, rb;

    // deadzones by Alejandro at Chris' request
    private static final double DEADZONE = 0;
    private static final double EXPONENT = 0;
    private static final double MAX_JOYSTICK = 1;

	/**
     * Initialize robot's motors
     */
    public DriveSubsystem(){

		lf = new TalonSRX(PortMap.LEFT_FRONT_SPARK);
		lm = new TalonSRX(PortMap.LEFT_MIDDLE_SPARK);
        lb = new TalonSRX(PortMap.LEFT_BACK_SPARK);
        
		rf = new TalonSRX(PortMap.RIGHT_FRONT_SPARK);
		rm = new TalonSRX(PortMap.RIGHT_MIDDLE_SPARK);
		rb = new TalonSRX(PortMap.RIGHT_BACK_SPARK);
	}

    /**
     * Processes a given joystick axis value to match the given deadzone and shape as determined by the given exponent.
     *
     * The equations were made by Bowen.
     * @param x raw axis input
     * @param deadzone given deadzone in either direction
     * @param exponent exponent of the power output curve. 0 is linear. Higher values give you more precision in the
     *                 lower ranges, and lower values give you more precision in the higher ranges.
     *                 I, Alejandro, recommend values between -1-deadzone and 2.
     * @param maxOutput The maximum output this method returns. Set it to the max joystick output for best results.
     * @return the processed axis value. Send this to the motors.
     */
    private double processAxis(double x, double deadzone, double exponent, double maxOutput) {
        // positive input between deadzone and max
        if (deadzone < x && x < maxOutput) {
            return (maxOutput / axisFunction(x, deadzone, exponent, maxOutput)) *
                    axisFunction(x, deadzone, exponent, maxOutput);
        // negative input between negative deadzone and negative max
        } else if (-maxOutput < x && x < -deadzone) {
            return (-maxOutput / axisFunction(x, deadzone, exponent, maxOutput)) *
                    axisFunction(-x, deadzone, exponent, maxOutput);
        // the input does not exceed the deadzone in either direction
        } else if (-deadzone < x && x < deadzone) {
            return 0;
        // somehow the input has exceeded the range of (-maxOutput, maxOutput)
        // this means that someone was playing with the code. Fix the maxOutput.
        } else {
            return x*Math.abs(x);
        }
    }

    /*
     * Used by processAxis as the main function of the curves.
     */
    private double axisFunction(double x, double d, double p, double m) {
        return Math.pow(x - d, p) * ((x - d) / (m - d));
    }

    public void setSpeed(Joystick leftStick, Joystick rightStick) {
        double left = -processAxis(leftStick.getY(), DEADZONE, EXPONENT, MAX_JOYSTICK);
        double right = -processAxis(rightStick.getY(), DEADZONE, EXPONENT, MAX_JOYSTICK);
        System.out.println("Left: " + leftStick.getY() + " " + left + " Right: " + rightStick.getY() + " " + right);
        setSpeedTank(left, right);
	}
	
	public void setSpeedRaw(Joystick leftStick, Joystick rightStick){
		setSpeedTank(-processAxis(leftStick.getY(), DEADZONE, EXPONENT, MAX_JOYSTICK),
                -processAxis(rightStick.getY(), DEADZONE, EXPONENT, MAX_JOYSTICK));
	}
        	
	public void setSpeedTank(double leftSpeed, double rightSpeed) {
		lf.set(ControlMode.PercentOutput, leftSpeed);
		lm.set(ControlMode.PercentOutput, leftSpeed);
		lb.set(ControlMode.PercentOutput, leftSpeed);

		rf.set(ControlMode.PercentOutput, -rightSpeed);
		rm.set(ControlMode.PercentOutput, -rightSpeed);
		rb.set(ControlMode.PercentOutput, -rightSpeed);
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
		setSpeedTankForwardManual(-processAxis(Robot.oi.leftStick.getY(), DEADZONE, EXPONENT, MAX_JOYSTICK),
                -processAxis(Robot.oi.rightStick.getY(), DEADZONE, EXPONENT, MAX_JOYSTICK), turnMagnitude);
	}

    @Override
    protected void initDefaultCommand() {
		//IGNORE THIS METHOD
    }
}