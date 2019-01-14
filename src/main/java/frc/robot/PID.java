package org.usfirst.frc.team1155.robot;

import edu.wpi.first.wpilibj.Timer;

public class PID {  

	Timer timer;
	double p, i, d, u, integral, prevTime, prevError;
	    
	public PID(double p, double i, double d) {
		timer = new Timer();
		timer.start();
		this.p = p;
		this.i = i;
		this.d = d;
		prevError = 0;
		prevTime = timer.get();
	}
	
	public void setP(double p) {
		this.p = p;
	}
	
	public void setI(double i) {
		this.i = i;
	}
	
	public void setD(double d) {
		this.d = d;
	}
	  
	public void add_measurement(double error) {
		double currentTime = timer.get();
	    double dt = currentTime - prevTime;
	    double dd = error - prevError;
	    integral += .5 * dt * (error + prevError);
	    double derivative = dd / dt;
	    u = p * error + d * derivative + i * integral;
	    prevTime = currentTime;
	    prevError = error;
	}
	  
	public void add_measurement_desired_speed(double error, double dDes) {
		double currentTime = timer.get();
	    double dt = currentTime - prevTime;
	    integral += .5 * dt * (error + prevError);
	    double derivative = (error - prevError) / dt;
	    u = p * error + d * (dDes - derivative) + i * integral;
	    prevTime = currentTime;
	    prevError = error;
	}
	  
	public double getOutput() {
		return u;
	}
}