package frc.robot.subsystems;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.PID;
import frc.robot.PortMap;

public class LiftSubsystem extends Subsystem {
    private PID liftPID;
    private double liftP = 0.06;
    private double liftI = 0;
    private double liftD = 0.0065;
    private double encoderAtTop = 0;
    private double encoderAtLevelOne = 0;
    private double encoderAtLevelTwo = 0;
    private double encoderAtLevelThree = 0;
    private CANSparkMax liftSpark;
    private CANEncoder liftEncoder;

    public PID getLiftPID() {
        return liftPID;
    }

    public void goToZero() {
        liftSpark = new CANSparkMax(PortMap.LIFT_SPARK, MotorType.kBrushless);
        liftPID = new PID(liftP, liftI, liftD);
        liftEncoder = liftSpark.getEncoder();
    }

    public void goToPositionOne() {
        liftPID.add_measurement(encoderAtLevelOne - getPos());
        liftSpark.set(liftPID.getLimitOutput(1));
    }

    public void goToPositionTwo() {
        liftPID.add_measurement(encoderAtLevelTwo - getPos());
        liftSpark.set(liftPID.getLimitOutput(1));
    }

    public void goToPositionThree() {
        liftPID.add_measurement(encoderAtLevelThree - getPos());
        liftSpark.set(liftPID.getLimitOutput(1));
    }

    private double getPos() {
        System.out.println(liftEncoder.getPosition());
        return liftEncoder.getPosition();
    }

    @Override
    protected void initDefaultCommand() {
    }
}
