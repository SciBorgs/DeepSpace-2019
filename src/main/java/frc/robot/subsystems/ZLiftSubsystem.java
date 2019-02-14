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
    private double maxOutput = 0.1;
    private double defaultSpeed = 0.08;
	
	// Initializes anglePID values
    private double angleP = 0.005, angleI = 0.0, angleD = 0.0;

    
    private DoubleSolenoid doubleSolenoid;

    public ZLiftSubsystem() {
        leftZLift = new TalonSRX(PortMap.LEFT_ZLIFT);
        rightZLift = new TalonSRX(PortMap.RIGHT_ZLIFT);

        pigeon = new PigeonIMU(rightZLift);
        anglePID = new PID(angleP, angleI, angleD);

        doubleSolenoid = new DoubleSolenoid(PortMap.FORWARD_CHANNEL, PortMap.REVERSE_CHANNEL);

        reset();
    }

    // Move Zlift using anglePID to maintain balance
    public void lift() {
        anglePID.add_measurement(getYaw());

        //System.out.println(anglePID.getOutput() + " Angle: " + getYaw());

        double speed = anglePID.getLimitOutput(maxOutput);
     
        leftZLift.set(ControlMode.PercentOutput, -speed + defaultSpeed);
        rightZLift.set(ControlMode.PercentOutput, speed + defaultSpeed);
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

    public void unlockPistons() {
        doubleSolenoid.set(DoubleSolenoid.Value.kForward);
    }
    
    // Lock and unlock pistons
    
    public void lockPistons(){
        doubleSolenoid.set(DoubleSolenoid.Value.kReverse);
    }

    @Override
    protected void initDefaultCommand() {
    }
}
