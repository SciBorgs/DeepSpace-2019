package frc.robot.subsystems;
import frc.robot.*;

import edu.wpi.first.wpilibj.command.Subsystem;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class LiftSubsystem extends Subsystem {
    PID liftPID;
    double liftP = 0.06;
    double liftI = 0;
    double liftD = 0.0065;
    double encoderAtTop = 0;
    double encoderAtLevelOne = 0;
    double encoderAtLevelTwo = 0;
    double encoderAtLevelThree = 0;
    CANSparkMax liftSpark;
    CANEncoder liftEncoder;
    
    public void goToZero() {
        liftSpark = new CANSparkMax(PortMap.LIFT_SPARK, MotorType.kBrushless);
        liftPID = new PID(liftP, liftI, liftD);
        liftEncoder = liftSpark.getEncoder();
    }

    public void goToPositionOne(){
        liftPID.add_measurement(encoderAtLevelOne - getPos());
        liftSpark.set(liftPID.getLimitOutput(1));
    }
    
    public void goToPositionTwo(){
        liftPID.add_measurement(encoderAtLevelTwo - getPos());
        liftSpark.set(liftPID.getLimitOutput(1));
    }

    public void goToPositionThree(){
        liftPID.add_measurement(encoderAtLevelThree - getPos());
        liftSpark.set(liftPID.getLimitOutput(1));
    }

    public double getPos() {
        System.out.println(liftEncoder.getPosition());
        return liftEncoder.getPosition();
    }

    @Override
    protected void initDefaultCommand() {}
}