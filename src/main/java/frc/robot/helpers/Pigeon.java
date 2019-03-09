package frc.robot.helpers;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.ctre.phoenix.sensors.PigeonIMU.CalibrationMode;

public class Pigeon {

    private PigeonIMU pigeon;
    public Pigeon (TalonSRX talon) {
        pigeon = new PigeonIMU(talon);
    }

    public PigeonIMU getPigeon() {
        return pigeon;
    }

    public double getAngle(){
        double[] yawPitchRoll = new double[3];
		getPigeon().getYawPitchRoll(yawPitchRoll);
        return Math.toRadians(yawPitchRoll[0]); //raw goes from 0 to 22 and we want from 0 to 360
	}
}