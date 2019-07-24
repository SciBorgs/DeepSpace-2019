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
    double TANK_ANGLE_P = .075, TANK_ANGLE_D = 0.0, TANK_ANGLE_I = 0;
    double GOAL_OMEGA_CONSTANT = 8; // Change this to change angle
    private double MAX_OMEGA_GOAL = 1 * GOAL_OMEGA_CONSTANT;
    public CANSparkMax lf, lm, lb, rf, rm, rb;
	private final String fileName = "DriveSubsystem.java";

    // deadzones by Alejandro at Chris' request. Graph them with the joystick function to understand the math.
    // https://www.desmos.com/calculator/ch19ahiwol
    private static final double INPUT_DEADZONE = 0.15; // deadzone because the joysticks are bad and they detect input when there is none
    private static final double MOTOR_MOVEPOINT = 0.07; // motor controller output that gets the wheels to turn
    private static final double EXPONENT = 10; // x^exponent to in the graph. x=0 is linear. x>0 gives more control in low inputs
    private static final double MAX_JOYSTICK = 1; // max joystick output value
    private static final double DEFAULT_MAX_JERK = 0.1; // Doesn't allow a motor's output to change by more than this in one tick
    private static final double STRAIGHT_DEADZONE = 0.15;
    private static final double STRAIGHT_EQUAL_INPUT_DEADZONE = 0; // If goal Omega is 0 and our regular input diff magnitude is less than this, the input diff goes to 0
    private PID tankAnglePID;
    public boolean assisted = false;
    public double driveMultiplier = 1;

    // d value so that when x=INPUT_DEADZONE the wheels move
    private static final double ALEJANDROS_CONSTANT = (MAX_JOYSTICK * Math.pow(MOTOR_MOVEPOINT / MAX_JOYSTICK, 1/(EXPONENT+1)) - INPUT_DEADZONE) /
                                                    (Math.pow(MOTOR_MOVEPOINT / MAX_JOYSTICK, 1/(EXPONENT+1)) - 1);


    private CANSparkMax newMotorObject(int port){
        return new CANSparkMax(port, MotorType.kBrushless);
    }

    public PID getTankAnglePID()   {return this.tankAnglePID;}
    public double getMaxOmegaGoal(){return MAX_OMEGA_GOAL;}

    public DriveSubsystem(){


		this.lf = newMotorObject(PortMap.LEFT_FRONT_SPARK);
		this.lm = newMotorObject(PortMap.LEFT_MIDDLE_SPARK);
        this.lb = newMotorObject(PortMap.LEFT_BACK_SPARK);
        
		this.rf = newMotorObject(PortMap.RIGHT_FRONT_SPARK);
		this.rm = newMotorObject(PortMap.RIGHT_MIDDLE_SPARK);
        this.rb = newMotorObject(PortMap.RIGHT_BACK_SPARK);

        this.lm.follow(this.lf);
        this.lb.follow(this.lf);

        this.rm.follow(this.rf);
        this.rb.follow(this.rf);

        this.tankAnglePID = new PID(TANK_ANGLE_P, TANK_ANGLE_I, TANK_ANGLE_D);
        Robot.logger.logFinalPIDConstants(this.fileName, "tank angle PID", this.tankAnglePID);
        Robot.logger.logFinalField(this.fileName, "input deadzone", INPUT_DEADZONE);
	}
    
	public void periodicLog(){
    }

	public CANSparkMax[] getSparks() {
        return new CANSparkMax[]{lf, lm, lb, rf, rm, rb};
    }

    // Used by processAxis as the main function of the curves.
    private double axisFunction(double x) {
        if (x < INPUT_DEADZONE){
            return 0;
        }
        double adjustedX = x - ALEJANDROS_CONSTANT;
        return Math.pow(adjustedX, EXPONENT) * ((adjustedX) / (MAX_JOYSTICK - ALEJANDROS_CONSTANT));
    }

    /**
     * Processes a given joystick axis value to match the given deadzone and shape as determined by the given exponent.
     *
     * The equations were made by Bowen.
     * @param x raw axis input
     * @return the processed axis value. Send this to the motors.
     */
    private double processAxis(double x) {
        return Utils.signOf(x) * Utils.signOf(MAX_JOYSTICK) * axisFunction(Math.abs(x));
    }

    public double deadzone(double output){
        if (Math.abs(output) < INPUT_DEADZONE){
            return 0;
        } else {
            return output;
        }
    }
    
    public double processStick(Joystick stick){
        return deadzone(-stick.getY());
    }

    public void assistedDriveMode(){this.assisted = true;}
    public void manualDriveMode()  {this.assisted = false;}

    public void setSpeed(Joystick leftStick, Joystick rightStick) {
        if (!this.assisted) {
            double leftInput  = processStick(leftStick);
            double rightInput = processStick(rightStick);
            setSpeedTankAngularControl(leftInput, rightInput);
        }
    }
	
	public void setSpeedRaw(Joystick leftStick, Joystick rightStick){
		setSpeedTank(processStick(leftStick),processStick(rightStick));
    }

    public double avgMotorInput(){
        return (this.lf.get() + this.rf.get())/2.0;
    }

    public double limitJerk(double oldSpeed, double newSpeed, double maxJerk){
        // Makes sure that the change in input for a motor is not more than maxJerk
        if (oldSpeed - newSpeed > maxJerk){
            return oldSpeed - maxJerk;
        } else if (newSpeed - oldSpeed > maxJerk){
            return oldSpeed + maxJerk;
        } else {
            return newSpeed;
        }
    }

    public void setMotorSpeed(CANSparkMax motor, double speed){setMotorSpeed(motor, speed, DEFAULT_MAX_JERK);}
    public void setMotorSpeed(TalonSRX motor, double speed)   {setMotorSpeed(motor, speed, DEFAULT_MAX_JERK);}

    public void setMotorSpeed(CANSparkMax motor, double speed, double maxJerk){
        speed = limitJerk(motor.get(), speed, maxJerk);
        //System.out.println("setting spark " + motor.getDeviceId() + " to " + speed);
        motor.set(speed);
    }
    public void setMotorSpeed(TalonSRX motor, double speed, double maxJerk){
        speed = limitJerk(motor.getMotorOutputPercent(), speed, maxJerk);
        //System.out.println("setting talon to " + speed);
        motor.set(ControlMode.PercentOutput, speed);
    }

    public void defaultDriveMultilpier(){this.driveMultiplier = 1;}
    public void setDriveMultiplier(double driveMultiplier){
        this.driveMultiplier = driveMultiplier;
    }
        	
	public void setSpeedTank(double leftSpeed, double rightSpeed) {
        setMotorSpeed(this.lf,  leftSpeed * this.driveMultiplier);
        setMotorSpeed(this.rf, -rightSpeed * this.driveMultiplier); // Possible needs to be negated
        Robot.logger.addData(this.fileName, "wheel output", this.lf.get(), DefaultValue.Previous);
    }
	
	public void setSpeedTankAngularControl(double leftSpeed, double rightSpeed) {
		double averageOutput = (leftSpeed + rightSpeed) / 2;
        double goalOmega = GOAL_OMEGA_CONSTANT * (rightSpeed - leftSpeed);
        // Makes it so if the two joysticks are close enough, it will try to go straight
        if (Math.abs(goalOmega) < STRAIGHT_DEADZONE){
            goalOmega = 0;
        } else {
            goalOmega -= Utils.signOf(goalOmega) * STRAIGHT_DEADZONE;
        }
        goalOmega = Utils.limitOutput(goalOmega, MAX_OMEGA_GOAL);
        double error = goalOmega - Robot.robotPosition.getAngularVelocity();
        tankAnglePID.addMeasurement(error);
        double inputDiff = tankAnglePID.getOutput();
        // If you are going almost straight and goalOmega is 0, it will simply give the same input to both wheels
        // We should test if this is beneficial
        if (goalOmega == 0 && (Math.abs(inputDiff) < STRAIGHT_EQUAL_INPUT_DEADZONE)){
            inputDiff = 0;
        }
        Robot.logger.addData(this.fileName, "input diff", inputDiff, DefaultValue.Empty);
        Robot.logger.addData(this.fileName, "error", error, DefaultValue.Empty);
		setSpeedTank(averageOutput - inputDiff, averageOutput + inputDiff); 
	}
	
	public void setSpeedTankForwardTurningPercentage(double forward, double turnMagnitude) {
        // Note: this controls dtheta/dx rather than dtheta/dt
		setSpeedTank(forward * (1 + turnMagnitude), forward * (1 - turnMagnitude));
    }
    
    public void setSpeedTankTurningPercentage(double turnMagnitude){
        double forward = (processStick(Robot.oi.leftStick) + processStick(Robot.oi.rightStick)) / 2;
        setSpeedTankForwardTurningPercentage(forward, turnMagnitude);
    }
	
	public void setSpeedTankForwardTorque(double forward, double torque) {
        // Note: this controls dtheta/dx rather than dtheta/dt
		setSpeedTank(forward + torque, forward - torque);
    }

    @Override
    protected void initDefaultCommand() {
		//IGNORE THIS METHOD
    }
}
