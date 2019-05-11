package frc.robot.helpers;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;

public class Pigeon {

    private PigeonIMU pigeonIMU;
    public Pigeon (TalonSRX talon) {
        this.pigeonIMU = new PigeonIMU(talon);
    }

    public PigeonIMU getPigeonIMU() {return this.pigeonIMU;}

    public double getAngle(){
        double[] yawPitchRoll = new double[3];
		this.pigeonIMU.getYawPitchRoll(yawPitchRoll);
        return Math.toRadians(yawPitchRoll[0]); //raw goes from 0 to 22 and we want from 0 to 360
    }
    
    public void setAngle(double angle){this.pigeonIMU.setYaw(angle);}
}