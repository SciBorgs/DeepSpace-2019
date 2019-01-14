package org.usfirst.frc.team1155.robot.subsystems;

import org.usfirst.frc.team1155.robot.Robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;

public class AutoSubsystem extends Subsystem {
	Timer timer;
	public TalonSRX pigeon;
	public AutoSubsystem()
	{
	}

	@Override
	protected void initDefaultCommand() {
		// TODO Auto-generated method stub
		
	}
	public double getPigeonAngle(){
		double[] yawPitchRoll = new double[3];
		Robot.pigeon.getYawPitchRoll(yawPitchRoll);
		//System.out.println("PigoenAngle: " + yawPitchRoll[0] % 360.);
		return Math.toRadians(yawPitchRoll[0] % 360.);
	}
	
	
    /* public void getVal() {
        System.out.println("The x-cordinate is " + getPos().get(0));
        
    } */

}