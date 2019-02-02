package frc.robot.subsystems;

import frc.robot.PortMap;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;
import com.ctre.phoenix.sensors.PigeonIMU;
import frc.robot.PID;

public class ZLiftSubsystem extends Subsystem {
    private TalonSRX leftZLift, rightZLift;
    private PigeonIMU pigeon;
    private PID anglePID;
    
    private double leftSpeed, rightSpeed = 0;
    private double angleP = 0.10, angleI = 0.0, angleD = 0.01;

    private DoubleSolenoid doubleSolenoid;

    public ZLiftSubsystem() {
        leftZLift = new TalonSRX(PortMap.LEFT_ZLIFT);
        rightZLift = new TalonSRX(PortMap.RIGHT_ZLIFT);

        pigeon = new PigeonIMU(rightZLift);        
        anglePID = new PID(angleP, angleI, angleD);
   
        doubleSolenoid = new DoubleSolenoid(PortMap.FORWARD_CHANNEL, PortMap.REVERSE_CHANNEL);

        reset();
    }

    public double safety(double s) {
        if (s > 0.02) {
            if (s > 0.06) {
                return 0.06;
            } else if (s < 0.05) {
                return 0.05;
            } else {
                return s;
            }
        } else if (s < -0.02) {
            if (s < -0.06) {
                return -0.06;
            } else if (s > -0.05) {
                return -0.05;
            } else {
                return s;
            }
        } else {
            return 0;
        }
    }

    public void lift() {
        anglePID.add_measurement(getYaw());

        System.out.println(anglePID.getOutput() + " Angle: " + getYaw());

        leftSpeed = safety(-anglePID.getOutput());
        rightSpeed = safety(anglePID.getOutput());
        leftSpeed += 0.051;
        rightSpeed += 0.051;
        leftZLift.set(ControlMode.PercentOutput, leftSpeed);
        rightZLift.set(ControlMode.PercentOutput, rightSpeed);
    }

    public void reset() {
        leftZLift.set(ControlMode.PercentOutput, 0);
        rightZLift.set(ControlMode.PercentOutput, 0);
        pigeon.setYaw(0, 10);

        doubleSolenoid.set(DoubleSolenoid.Value.kReverse);
    }

    private double getYaw() {
        double[] angs = new double[3];
        pigeon.getYawPitchRoll(angs);
        return angs[0];
    }

    public void unlockPistons() { doubleSolenoid.set(DoubleSolenoid.Value.kForward); }

    @Override
    protected void initDefaultCommand() {}
}