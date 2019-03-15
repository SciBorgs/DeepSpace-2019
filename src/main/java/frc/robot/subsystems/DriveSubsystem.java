package frc.robot.subsystems;

import frc.robot.PortMap;
import frc.robot.Robot;
import frc.robot.helpers.PID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Subsystem;

import com.revrobotics.CANSparkMax;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class DriveSubsystem extends Subsystem {
    // Define tested error values here
    double tankAngleP = .3, tankAngleD = 0.2, tankAngleI = 0;
    double goalOmegaConstant = 2;
    double maxOmegaGoal = 1 * goalOmegaConstant;
    public CANSparkMax lf, lm, lb, rf, rm, rb;

    // deadzones by Alejandro at Chris' request. Graph them with the joystick function to understand the math.
    // https://www.desmos.com/calculator/ch19ahiwol
    private static final double INPUT_DEADZONE = 0.11; // deadzone because the joysticks are bad and they detect input when there is none
    private static final double MOTOR_MOVEPOINT = 0.07; // motor controller output that gets the wheels to turn
    private static final double EXPONENT = 10; // x^exponent to in the graph. x=0 is linear. x>0 gives more control in low inputs
    private static final double MAX_JOYSTICK = 1; // max joystick output value
    private static final double DEFAULT_MAX_JERK = 0.1; // Doesn't allow a motor's output to change by more than this in one tick
    private static final double GEAR_SHIFT_OFFSET = 0.2;
    private static final double GEAR_SHIFT_DEADZONE = 0.1;
    private static final double GEAR_SHIFT_FUNC_POWER = 1.4;
    private static final double HIGH_REDUCTION_END = 0.6;
    private static final double LOW_REDUCTION_START = 0.5;
    private boolean highReduction = true;
    private PID tankAnglePID;

    // d value so that when x=INPUT_DEADZONE the wheels move
    private static final double ALEJANDROS_CONSTANT = (MAX_JOYSTICK * Math.pow(MOTOR_MOVEPOINT / MAX_JOYSTICK, 1/(EXPONENT+1)) - INPUT_DEADZONE) /
                                                    (Math.pow(MOTOR_MOVEPOINT / MAX_JOYSTICK, 1/(EXPONENT+1)) - 1);

    private CANSparkMax newMotorObject(int port){
        return new CANSparkMax(port, MotorType.kBrushless);
    }

	/**
     * Initialize robot's motors
     */
    public DriveSubsystem(){


		lf = newMotorObject(PortMap.LEFT_FRONT_SPARK);
		lm = newMotorObject(PortMap.LEFT_MIDDLE_SPARK);
        //lb = newMotorObject(PortMap.LEFT_BACK_SPARK); // UNCOMMENT FOR ACTUAL ROBOT. COMMENTED OUT FOR PRACTICE B/C FALTY SPARK
        
		rf = newMotorObject(PortMap.RIGHT_FRONT_SPARK);
		rm = newMotorObject(PortMap.RIGHT_MIDDLE_SPARK);
        //rb = newMotorObject(PortMap.RIGHT_BACK_SPARK); // UNCOMMENT FOR ACTUAL ROBOT. COMMENTED OUT FOR PRACTICE B/C FALTY SPARK

        lm.follow(lf);
        // lb.follow(lf); // UNCOMMENT FOR ACTUAL ROBOT. COMMENTED OUT FOR PRACTICE B/C FALTY SPARK

        rm.follow(rf);
        //rb.follow(rf); // UNCOMMENT FOR ACTUAL ROBOT. COMMENTED OUT FOR PRACTICE B/C FALTY SPARK

        tankAnglePID = new PID(tankAngleP, tankAngleI, tankAngleD);
	}

    /**
     * Processes a given joystick axis value to match the given deadzone and shape as determined by the given exponent.
     *
     * The equations were made by Bowen.
     * @param x raw axis input
     * @return the processed axis value. Send this to the motors.
     */
    private double processAxis(double x) {
        double sign = (x == 0) ? 1 : (Math.abs(x) / x);
        return sign * (MAX_JOYSTICK / axisFunction(MAX_JOYSTICK)) * axisFunction(Math.abs(x));
    }

    // Used by processAxis as the main function of the curves.
    private double axisFunction(double x) {
        if (x < INPUT_DEADZONE){
            return 0;
        }
        double adjustedX = x - ALEJANDROS_CONSTANT;
        return Math.pow(adjustedX, EXPONENT) * ((adjustedX) / (MAX_JOYSTICK - ALEJANDROS_CONSTANT));
    }
    
    public double processStick(Joystick stick){
        return processAxis(-stick.getY());
    }

    public double processFunc(double x){
        return (1 + GEAR_SHIFT_OFFSET) * Math.pow(Math.abs(x - GEAR_SHIFT_DEADZONE), GEAR_SHIFT_FUNC_POWER)
                / Math.pow((1 - GEAR_SHIFT_DEADZONE),GEAR_SHIFT_FUNC_POWER);
    }

    public double processStickHighReduction(double input){
        highReduction = true;
        if(input >= GEAR_SHIFT_DEADZONE){
            return processFunc(input);
        }else if(input <= -GEAR_SHIFT_DEADZONE){
            return -Math.abs(processFunc(input + 2 * GEAR_SHIFT_DEADZONE));
        }
        return 0;
    }

    public double processStickLowReduction(double input){
        highReduction = false;
        if(input >= LOW_REDUCTION_START){
            return processFunc(input) - GEAR_SHIFT_OFFSET;
        }else if(input <= -LOW_REDUCTION_START){
            return -Math.abs(processFunc(input + 2 * GEAR_SHIFT_DEADZONE)) + GEAR_SHIFT_OFFSET;
        }else{
            System.out.println("[ [ [ LOW REDUCTION JOYSTICK ERROR ] ] ]");
        }
        return 0;
    }

    public double processStickGearShift(Joystick stick){
        double input = -stick.getY();
        if(highReduction){
            if(Math.abs(input) >= HIGH_REDUCTION_END){
                return processStickLowReduction(input);
            }
            return processStickHighReduction(input);
        }else{
            if(Math.abs(input) <= LOW_REDUCTION_START){
                return processStickHighReduction(input);
            }
            return processStickLowReduction(input);
        }
    }

    public void setSpeed(Joystick leftStick, Joystick rightStick) {
        double left  = processStick(leftStick);
        double right = processStick(rightStick);

        //double left = processStickGearShift(leftStick);
        //double right = processStickGearShift(rightStick);

        //System.out.println("Left: " + leftStick.getY() + " " + left + " Right: " + rightStick.getY() + " " + right);
        setSpeedTankAngularControl(left, right);
    }
	
	public void setSpeedRaw(Joystick leftStick, Joystick rightStick){
		setSpeedTank(processStick(leftStick),processStick(rightStick));
    }

    public double limitJerk(double oldSpeed, double newSpeed, double maxJerk){
        if (oldSpeed - newSpeed > maxJerk){
            return oldSpeed - maxJerk;
        } else if (newSpeed - oldSpeed > maxJerk){
            return oldSpeed + maxJerk;
        } else {
            return newSpeed;
        }
    }

    public void setMotorSpeed(CANSparkMax motor, double speed){
        setMotorSpeed(motor, speed, DEFAULT_MAX_JERK);
    }
    public void setMotorSpeed(CANSparkMax motor, double speed, double maxJerk){
        speed = limitJerk(motor.get(), speed, maxJerk);
        //System.out.println("setting spark " + motor.getDeviceId() + " to " + speed);
        motor.set(speed);
    }

    public void setMotorSpeed(TalonSRX motor, double speed){
        setMotorSpeed(motor, speed, DEFAULT_MAX_JERK);
    }
    public void setMotorSpeed(TalonSRX motor, double speed, double maxJerk){
        speed = limitJerk(motor.getMotorOutputPercent(), speed, maxJerk);
        //System.out.println("setting talon to " + speed);
        motor.set(ControlMode.PercentOutput, speed);
    }
        	
	public void setSpeedTank(double leftSpeed, double rightSpeed) {
        setMotorSpeed(lf, leftSpeed * 1.5); // GET RID OF 1.5 MULTIPLIER FOR ACTUAL ROBOT. COMMENTED OUT FOR PRACTICE B/C FALTY SPARK
        setMotorSpeed(rf, -rightSpeed * 1.5);
	}
	
	public void setSpeedTankAngularControl(double leftSpeed, double rightSpeed) {
		double averageOutput = (leftSpeed + rightSpeed) / 2;
        double goalOmega = Math.min(goalOmegaConstant * (rightSpeed - leftSpeed), maxOmegaGoal);
        System.out.println("angular speed: " + Robot.positioningSubsystem.getAngularSpeed());
        System.out.println("desired angular speed: " + goalOmega);
        double error = goalOmega - Robot.positioningSubsystem.getAngularSpeed();
        tankAnglePID.add_measurement(error);
        double change = tankAnglePID.getOutput();
        System.out.println("Output: " + change);
		setSpeedTank(averageOutput - change, averageOutput + change); 
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
