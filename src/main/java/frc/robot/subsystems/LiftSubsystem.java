package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;
import frc.robot.PortMap;
import frc.robot.Robot;
import frc.robot.Utils;
import frc.robot.helpers.PID;
import frc.robot.logging.Logger.DefaultValue;

import java.util.Hashtable;

public class LiftSubsystem extends Subsystem {
    public enum Target { High, Mid, Low, Ground, Initial }
    public CANSparkMax liftSpark;
    public TalonSRX    armTiltTalon;
    public boolean manualArmMode     = true;
    public boolean manualCascadeMode = true;

    private DigitalInput cascadeAtBottomLimitSwitch, armAtTopSwitch;
    private PID armPID;
    private PID liftPID;
    private SimpleWidget levelCounterWidget;
    private boolean previousLiftLimitSwitch = false;
    private boolean previousArmLimitSwitch  = false;
    private boolean movingLift = false;
    private boolean tiltingArm = false;
    private double ARM_OUTPUT_LIMIT = 1;
    private double ARM_P = 0.62, ARM_I = 0.0, ARM_D = 0, LIFT_P = 1, LIFT_I = 0.0, LIFT_D = 0.05;
    private double offsetCascadeHeight = 0;
    private double offsetArmAngle = 0;
    private int levelCounter = 2;

    private final String FILENAME = "LiftSubsystem.java";
    private final Hashtable<Target, Integer> HATCH_POSITIONS = // Gives how many hatches above the lowest one for each
    new Hashtable<>() {
        private static final long serialVersionUID = 5134778529966511317L;
        {
            put(Target.High, 2);
            put(Target.Mid,  1);
            put(Target.Low,  0);
        }
    };
    private final double SPARK_ENCODER_WHEEL_RATIO = 1 / 20.0;  // For the cascade
    private final double TALON_ENCODER_WHEEL_RATIO = 24.0 / 56; // For the carriage
    private final double LIFT_WHEEL_RADIUS = Utils.inchesToMeters(.75); // In meters, the radius of the wheel that is
                                                                        // pulling up the lift
    private final double LIFT_STATIC_INPUT = 0.018;
    private final double ARM_STATIC_INPUT  = 0.065;
    private final double MAX_CARRIAGE_ADDED_SPEED = .18;
    private final double MINIMUM_CARRIAGE_INPUT   = 0.025;
    private final double MINIMUM_CASCADE_INPUT    = .005;
    private final double ROCKET_HATCH_GAP = Utils.inchesToMeters(28);
    private final double LOW_HATCH_HEIGHT = Utils.inchesToMeters(17);
    private final double MAX_HINGE_HEIGHT = .97;
    private final double HATCH_TO_CARGO_DEPOSIT = Utils.inchesToMeters(8.5);
    private final double ARM_MAX_ANGLE    = Math.toRadians(67);
    private final double ARM_TARGET_ANGLE = Math.toRadians(30);
    private final double ARM_LENGTH    = Utils.inchesToMeters(20);
    private final double BOTTOM_HEIGHT = Utils.inchesToMeters(9); // In meters, the height at the lift's lowest point
    private final double INITIAL_GAP_TO_GROUND = Utils.inchesToMeters(0); // How far up the intake should be when it's
                                                                          // sucking in cargo
    private final double RESTING_ANGLE = Math.asin((INITIAL_GAP_TO_GROUND - BOTTOM_HEIGHT) / ARM_LENGTH); // In radians
    private final double HEIGHT_PRECISION = 0.05; // In meters
    private final double ANGLE_PRECISION  = Math.toRadians(2);
    private final double INITIAL_ANGLE  = ARM_MAX_ANGLE; // In reality should be 60ish
    private final double INITIAL_HEIGHT = BOTTOM_HEIGHT;
    private final double SLOW_LIFT_INPUT = .3; // An input that should move the lift slowly, not for in game purposes
    private final double SLOW_ARM_INPUT  = .3;  // An input that should move the arm slowly, not for in game purposes
    private final int LIFT_PID_SMOOTHNESS = 3; // Probably change to 4
    private final int ARM_PID_SMOOTHNESS  = 7;
    private final int MIN_LEVEL = 0;
    private final int MAX_LEVEL = 2;

    public void initDefaultCommand(){}

    public LiftSubsystem() {
        this.cascadeAtBottomLimitSwitch = new DigitalInput(PortMap.CASCADE_AT_BOTTOM_LIMIT_SWITCH);
        this.armAtTopSwitch             = new DigitalInput(PortMap.ARM_AT_TOP_LIMIT_SWITCH);
        this.armPID                     = new PID(ARM_P, ARM_I, ARM_D);
        this.armTiltTalon               = new TalonSRX(PortMap.ARM_TILT_TALON);
        this.armTiltTalon.setNeutralMode(NeutralMode.Brake);
        this.armPID.setSmoother(ARM_PID_SMOOTHNESS);
        this.liftSpark = new CANSparkMax(PortMap.LIFT_SPARK, MotorType.kBrushless);
        this.liftPID   = new PID(LIFT_P, LIFT_I, LIFT_D);
        this.liftPID.setSmoother(LIFT_PID_SMOOTHNESS);

        ShuffleboardTab levelCounterTab = Shuffleboard.getTab("Level Counter");
        this.levelCounterWidget = levelCounterTab.add("Level Counter", -1).withWidget("Text View").withPosition(1, 0)
                .withSize(2, 2);

        realLiftHeightIs(INITIAL_HEIGHT);
        realArmAngleIs(INITIAL_ANGLE);

        Robot.logger.logFinalPIDConstants(FILENAME, "arm PID", this.armPID);
        Robot.logger.logFinalPIDConstants(FILENAME, "lift PID", this.liftPID);
    }

    public  PID           getArmPID()                 {return this.armPID;}
    public  PID           getLiftPID()                {return this.liftPID;}
    public  CANSparkMax   getLiftSpark()              {return this.liftSpark;}
    public  CANSparkMax[] getSparks()                 {return new CANSparkMax[]{this.liftSpark};}
    public  TalonSRX[]    getTalons()                 {return new TalonSRX[]{this.armTiltTalon};}

    public  void 		  manualArmMode()             {this.manualArmMode = true;}
    public  void 		  autoArmMode()               {this.manualArmMode = false;}
    public  void 		  manualCascadeMode()         {this.manualCascadeMode = true;}
    public  void 		  autoCascadeMode()           {this.manualCascadeMode = false;}

    public  void          moveLevelCounter(int change){setLevelCounter(this.levelCounter + change);}
    public  boolean       updateLevelCounterWidget()  {return levelCounterWidget.getEntry().setNumber(this.levelCounter);}

    public  void          currentlyTiltingArm()       {this.tiltingArm = true;}
    public  boolean       isStatic()                  {return !(this.tiltingArm || this.movingLift);}

    public void periodicLog() {
        Robot.logger.addData(FILENAME, "carriage angle", getArmAngle(), DefaultValue.Previous);
        Robot.logger.addData(FILENAME, "current height", getLiftHeight(), DefaultValue.Previous);
    }

    private double getTargetHeight(Target target) {
        double hatchTargetHeight = LOW_HATCH_HEIGHT + HATCH_POSITIONS.get(target) * ROCKET_HATCH_GAP;
        double cargoTargetHeight = hatchTargetHeight + HATCH_TO_CARGO_DEPOSIT;
        // Target height will go to the defualt cargo_height if it is holding a cargo,
        // otherwise it will go to the hatch height
        if (Robot.intakeSubsystem.holdingCargo()) {
            return cargoTargetHeight;
        }
        return hatchTargetHeight;
    }

    private void setLiftSpeedRaw(double speed){Robot.driveSubsystem.setMotorSpeed(this.liftSpark, speed);}

    private void moveLiftToBottom(){conditionalSetLiftSpeed(-SLOW_LIFT_INPUT, !liftAtBottom());}

    public void moveLiftToHeight(double targetLiftHeight) {
        Robot.logger.addData(FILENAME, "target lift height (m)", targetLiftHeight, DefaultValue.Empty);

        double error             = targetLiftHeight - getLiftHeight();
        boolean hitCorrectHeight = Math.abs(error) < HEIGHT_PRECISION;
        liftPID.addMeasurement(error);

        if (hitCorrectHeight && targetLiftHeight == INITIAL_HEIGHT) {
            setLiftSpeedRaw(0);
        } else {
            setLiftSpeed(liftPID.getOutput());
        }
    }

    private void moveArmToMaxAngle(){conditionalSetArmTiltSpeed(SLOW_ARM_INPUT, !armAtMaxAngle());}

    public void moveArmToAngle(double targetAngle) {
        Robot.logger.addData(FILENAME, "target angle (deg)", Math.toDegrees(targetAngle), DefaultValue.Empty);

        double  error            = targetAngle - getArmAngle();
        boolean hitCorrectAngle  = Math.abs(error) < ANGLE_PRECISION;
        double  output           = armPID.getLimitedOutput(ARM_OUTPUT_LIMIT);
        armPID.addMeasurement(error);

        if (targetAngle == ARM_MAX_ANGLE && !armAtMaxAngle()) {
            output += MAX_CARRIAGE_ADDED_SPEED;
            output  = Math.max(SLOW_ARM_INPUT, output);
        }
        setArmTiltSpeed(output);

        // TODO: I do not believe that we need what is below. We need to test it
        if (hitCorrectAngle && (targetAngle == ARM_MAX_ANGLE)) moveArmToMaxAngle();
    }

    public void moveToPosition(double targetAngle, double targetLiftHeight) {
        moveArmToAngle(targetAngle);
        moveLiftToHeight(targetLiftHeight);
    }

    private boolean needsCascade(double height){return height > (ARM_LENGTH * Math.sin(ARM_TARGET_ANGLE) + BOTTOM_HEIGHT);}

    public double getTargetLiftHeight(double depositHeight) {
        if (needsCascade(depositHeight)) {
            return Math.min(depositHeight - ARM_LENGTH * Math.sin(ARM_TARGET_ANGLE), MAX_HINGE_HEIGHT);
        }
        return BOTTOM_HEIGHT;
    }

    public void moveToHeight(double targetHeight) {
        double targetLiftHeight = getTargetLiftHeight(targetHeight);
        double targetAngle      = Math.asin((targetHeight - targetLiftHeight) / ARM_LENGTH);
        moveToPosition(targetAngle, targetLiftHeight);
    }

    public void moveArmToTarget(Target target) {
        double targetHeight;
        // Ground - 0; Low(hatch) - 19 inches; High(cargo) - 27 inches;
        if (target == Target.Ground) {
            targetHeight = 0;
        } else if (target == Target.Low) {
            targetHeight = Utils.inchesToMeters(14.5);
        } else {
            targetHeight = Utils.inchesToMeters(29);
        }

        double targetAngle = Math.asin((targetHeight - BOTTOM_HEIGHT) / ARM_LENGTH);
        if (target == Target.Mid) targetAngle = ARM_MAX_ANGLE;
        moveArmToAngle(targetAngle);
    }

    public void moveToTarget(Target target) {
        if (target == Target.Ground) {
            moveToPosition(RESTING_ANGLE, BOTTOM_HEIGHT);
        }
        else if (target == Target.Initial) {
            moveToInitial();
        } else {
            moveToHeight(getTargetHeight(target));
        }
    }

    public void moveToInitial() {
        moveLiftToBottom();
        moveArmToMaxAngle();
    }

    public void setLevelCounter(int level) {
        this.levelCounter = Math.max(level, MIN_LEVEL);
        this.levelCounter = Math.min(this.levelCounter, MAX_LEVEL);
        liftPID.reset();
        armPID.reset();
    }

    public Target getTarget() {
        switch (this.levelCounter) {
            case 0:  return Target.Ground;
            case 1:  return Target.Low;
            case 2:  return Target.Mid;
            case 3:  return Target.High;
            default: return Target.Ground;
        }
    }

    public double getUnadjustedLiftHeight() {
        return SPARK_ENCODER_WHEEL_RATIO * Robot.encoderSubsystem.getSparkAngle(this.liftSpark) * LIFT_WHEEL_RADIUS
                + BOTTOM_HEIGHT + this.offsetCascadeHeight;
    }

    private void realLiftHeightIs(double height){this.offsetCascadeHeight += INITIAL_HEIGHT - getUnadjustedLiftHeight();}

    private boolean liftAtTop(){return getLiftHeight() > MAX_HINGE_HEIGHT;}

    public boolean liftAtBottom() {
        boolean currentOutput = !cascadeAtBottomLimitSwitch.get();
        boolean end = currentOutput && this.previousLiftLimitSwitch;
        this.previousLiftLimitSwitch = currentOutput;
        return end;
    }

    public double getLiftHeight() {
        if (liftAtBottom()) {
            realLiftHeightIs(BOTTOM_HEIGHT);
            return BOTTOM_HEIGHT;
        }
        return getUnadjustedLiftHeight();
    }

    public double getUnadjustedArmAngle() {
        return TALON_ENCODER_WHEEL_RATIO * Robot.encoderSubsystem.getTalonAngle(this.armTiltTalon) + RESTING_ANGLE
                + this.offsetArmAngle;
    }

    private void realArmAngleIs(double angle){this.offsetArmAngle += angle - getUnadjustedArmAngle();}

    public boolean armAtMaxAngle() {
        boolean currentOutput = !armAtTopSwitch.get();
        boolean end = currentOutput && this.previousArmLimitSwitch;
        this.previousArmLimitSwitch = currentOutput;
        return end;
    }

    public double getArmAngle() {
        if (armAtMaxAngle()) {
            realArmAngleIs(INITIAL_ANGLE);
            return INITIAL_ANGLE;
        }
        return getUnadjustedArmAngle();
    }

    private void conditionalSetLiftSpeed(double speed, boolean b) {
        // If the boolean is true it will simply do a set lift speed. Otherwise, it will
        // set it to zero
        if (b) setLiftSpeed(speed);
        else   setLiftSpeed(0);
    }

    private void conditionalSetArmTiltSpeed(double speed, boolean b) {
        // If the boolean is true it will simply do a set arm tilt speed. Otherwise, it
        // will set it to zero
        if (b) setArmTiltSpeed(speed);
        else   setArmTiltSpeed(0);
    }

    public void setLiftSpeed(double speed) {
        if (liftAtTop()) {
            System.out.println("preventing you from going up");
            speed = Math.min(speed, 0);
        }
        this.movingLift = Math.abs(speed) > MINIMUM_CASCADE_INPUT;
        setLiftSpeedRaw(speed + LIFT_STATIC_INPUT);
    }

    public void setArmTiltSpeed(double speed) {
        Robot.logger.addData(FILENAME, "arm input", speed, DefaultValue.Previous);

        this.tiltingArm = Math.abs(speed) > MINIMUM_CARRIAGE_INPUT;
        if (armAtMaxAngle() && speed > 0) speed = 0;
        Robot.driveSubsystem.setMotorSpeed(this.armTiltTalon, speed + ARM_STATIC_INPUT, 1);
    }
}