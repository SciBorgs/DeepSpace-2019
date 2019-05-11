
package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.command.Subsystem;

public class EncoderSubsystem extends Subsystem {
    
    public static final double NEO_TICKS_PER_REV = 1; // For sparks
    public static final double ENC_TICKS_PER_REV = 4096; // For talons
    private final String fileName = "positioningSubsystem.java";

    public EncoderSubsystem(){}

    public double getTalonAngle(TalonSRX talon){
        return talon.getSensorCollection().getQuadraturePosition() / ENC_TICKS_PER_REV * 2 * Math.PI;
    }
	public double getSparkAngle(CANSparkMax spark){
		return spark.getEncoder().getPosition() / NEO_TICKS_PER_REV * 2 * Math.PI;
	}
    
	public void periodicLog(){
	}

    @Override
    protected void initDefaultCommand() {
        // LITTERALLY DIE
    }
}