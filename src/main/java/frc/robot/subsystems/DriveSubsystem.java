package frc.robot.subsystems;

import frc.robot.PortMap;
import frc.robot.Robot;
import frc.robot.Utils;
import frc.robot.helpers.PID;
import frc.robot.logging.Logger.DefaultValue;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Subsystem;

import com.revrobotics.CANSparkMax;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class DriveSubsystem extends Subsystem {
    // Define tested error values here
    double TANK_ANGLE_P = .06, TANK_ANGLE_D = 0.0, TANK_ANGLE_I = 0;
    double goalOmegaConstant = 68; // Change this to change angle
    private double[] maxOmegaGoal = {1 * goalOmegaConstant}; // must be an array so it's mutable
    public CANSparkMax lf, lm, lb, rf, rm, rb;
	private final String fileName = "DriveSubsystem.java";

    // deadzones by Alejandro at Chris' request. Graph them with the joystick function to understand the math.
    // https://www.desmos.com/calculator/ch19ahiwol
    private static final double INPUT_DEADZONE = 0.11; // deadzone because the joysticks are bad and they detect input when there is none
    private static final double MOTOR_MOVEPOINT = 0.07; // motor controller output that gets the wheels to turn
    private static final double EXPONENT = 10; // x^exponent to in the graph. x=0 is linear. x>0 gives more control in low inputs
    private static final double MAX_JOYSTICK = 1; // max joystick output value
    private static final double DEFAULT_MAX_JERK = 0.1; // Doesn't allow a motor's output to change by more than this in one tick
    private static final double GEAR_SHIFT_OFFSET = 0.3;
    private static final double GEAR_SHIFT_DEADZONE = 0.1;
    private static final double GEAR_SHIFT_FUNC_POWER = 1.4;
    private static final double HIGH_REDUCTION_END = 0.8;
    private static final double LOW_REDUCTION_START = 0.4;
    private static final double STRAIGHT_DEADZONE = 0.15;
    private boolean highReduction = true;
    private PID tankAnglePID;
    public boolean assisted = false;
    public double driveMultiplier = 1;

    // d value so that when x=INPUT_DEADZONE the wheels move
    private static final double ALEJANDROS_CONSTANT = (MAX_JOYSTICK * Math.pow(MOTOR_MOVEPOINT / MAX_JOYSTICK, 1/(EXPONENT+1)) - INPUT_DEADZONE) /
                                                    (Math.pow(MOTOR_MOVEPOINT / MAX_JOYSTICK, 1/(EXPONENT+1)) - 1);


    private CANSparkMax newMotorObject(int port){
        return new CANSparkMax(port, MotorType.kBrushless);
    }

    public PID getTankAnglePID() {
        return tankAnglePID;
    }

    public double[] getMaxOmegaGoal() {
        return maxOmegaGoal;
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

        lm.follow(lf);
        lb.follow(lf);

        rm.follow(rf);
        rb.follow(rf);

        tankAnglePID = new PID(TANK_ANGLE_P, TANK_ANGLE_I, TANK_ANGLE_D);
        Robot.logger.logFinalPIDConstants(this.fileName, "tank angle PID", tankAnglePID);
        Robot.logger.logFinalField(this.fileName, "input deadzone", INPUT_DEADZONE);
	}
    
	public void periodicLog(){
    }

	public CANSparkMax[] getSparks() {
        return new CANSparkMax[]{lf, lm, lb, rf, rm, rb};
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

    public void assistedDriveMode(){assisted = true;}
    public void manualDriveMode(){assisted = false;}

    public void setSpeed(Joystick leftStick, Joystick rightStick) {
        if (!assisted) {
            double left  = processStick(leftStick);
            double right = processStick(rightStick);
            //double left = processStickGearShift(leftStick);
            //double right = processStickGearShift(rightStick);

            //System.out.println("Left: " + leftStick.getY() + " " + left + " Right: " + rightStick.getY() + " " + right);
            setSpeedTankAngularControl(left, right);
            //setSpeedTank(left,right);
        }
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
        //System.System.out.println("setting spark " + motor.getDeviceId() + " to " + speed);
        motor.set(speed);
    }

    public void setMotorSpeed(TalonSRX motor, double speed){
        setMotorSpeed(motor, speed, DEFAULT_MAX_JERK);
    }
    public void setMotorSpeed(TalonSRX motor, double speed, double maxJerk){
        speed = limitJerk(motor.getMotorOutputPercent(), speed, maxJerk);
        //System.out.println("setting talon to " + speed);
        motor.set(ControlMode.PercentOutput, speed);
        //System.out.println("checking: " + motor.getMotorOutputPercent());
    }

    public void defaultTankMultilpier(){driveMultiplier = 1;}
    public void setTankMultiplier(double driveMultiplier){
        this.driveMultiplier = driveMultiplier;
    }
        	
	public void setSpeedTank(double leftSpeed, double rightSpeed) {
        /*if(Math.abs(leftSpeed) == 1){
            System.out.println("[   [   [   SPARK SPEED LEFT     ]   ]   ]");
            System.out.println(lf.getEncoder().getVelocity() / 4096);
            //System.out.println(Robot.positioningSubsystem.getWheelSpeed(lf));
        }
        if(Math.abs(rightSpeed) == 1){
            System.out.println("[   [   [   SPARK SPEED RIGHT     ]   ]   ]");
            System.out.println(rf.getEncoder().getVelocity() / 4096);
            //System.out.println(Robot.positioningSubsystem.getWheelSpeed(rf));
        }*/
        setMotorSpeed(lf, leftSpeed * driveMultiplier);
        setMotorSpeed(rf, -rightSpeed * driveMultiplier); // Possible needs to be negated
        Robot.logger.addData(this.fileName, "wheel output", lf.get(), DefaultValue.Previous);
    }
	
	public void setSpeedTankAngularControl(double leftSpeed, double rightSpeed) {
		double averageOutput = (leftSpeed + rightSpeed) / 2;
        double goalOmega = goalOmegaConstant * (rightSpeed - leftSpeed);
        if (Math.abs(goalOmega) < STRAIGHT_DEADZONE){
            goalOmega = 0;
        } else if (goalOmega < 0) {
            goalOmega += STRAIGHT_DEADZONE;
        } else {
            goalOmega -= STRAIGHT_DEADZONE;
        }
        goalOmega = Utils.limitOutput(goalOmega, maxOmegaGoal[0]);
        //System.out.println("angular speed: " + Robot.positioningSubsystem.getAngularSpeed());
        //System.out.println("desired angular speed: " + goalOmega);
        double error = goalOmega - Robot.positioningSubsystem.getAngularSpeed();
        tankAnglePID.add_measurement(error);
        double inputDiff = tankAnglePID.getOutput();
        Robot.logger.addData(this.fileName, "input diff", inputDiff, DefaultValue.Empty);
        Robot.logger.addData(this.fileName, "error", error, DefaultValue.Empty);
        //System.out.println("Output: " + change);
		setSpeedTank(averageOutput - inputDiff, averageOutput + inputDiff); 
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
