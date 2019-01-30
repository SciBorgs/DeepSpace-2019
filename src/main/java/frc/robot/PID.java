package frc.robot;

import edu.wpi.first.wpilibj.Timer;
import java.util.ArrayList;

public class PID {  

	Timer timer;
	private ArrayList<Double> times, errors;
	private int maxSize = 4;
	public double p, i, d, u, integral;
	    
	public PID(double p, double i, double d) {
		timer = new Timer();
		timer.start();
		times  = new ArrayList<Double>();
		errors = new ArrayList<Double>();
		this.p = p;
		this.i = i;
		this.d = d;
	}
	
	public ArrayList<Double> getErrors(){
		return errors;
	}
	
	public ArrayList<Double> getTimes(){
		return times;
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
	
	public void setSmoother(int amount) {maxSize = amount;}
	  
	public void add_measurement(double error) {
		double currentTime = timer.get();
		if (!(errors.isEmpty())) {
		    double dt = currentTime - times.get(0);
		    double dd =  error - errors.get(0);
		    integral += .5 * dt * (error + errors.get(0));
		    double derivative = dd / dt;
		    u = p * error + d * derivative + i * integral;
		}
		else
			u = p * error;
		Robot.pos.trimAdd(times, currentTime, maxSize);
		Robot.pos.trimAdd(errors, error, maxSize);
	}
	  
	public void add_measurement_d(double error, double dd) {
		double currentTime = timer.get();
		if (!(errors.isEmpty())) {
		    double dt = currentTime - times.get(0);
		    integral += .5 * dt * (error + errors.get(0));
		    double derivative = dd / dt;
		    u = p * error + d * derivative + i * integral;
		}
		else
			u = p * error;
		Robot.pos.trimAdd(times, currentTime, maxSize);
		Robot.pos.trimAdd(errors, error, maxSize);
	}
	  
	public void add_measurement_desired_speed(double error, double dDes) { // Try to refrain from using this GARBAGE method
		double currentTime = timer.get();
	    double dt = currentTime - times.get(0);
	    integral += .5 * dt * (error + errors.get(0));
	    double derivative = (errors.isEmpty()? 0 : (error - errors.get(0))) / dt;
	    u = p * error + d * (dDes - derivative) + i * integral;
	    Robot.pos.trimAdd(times, currentTime, maxSize);
	    errors.add(0, error);
	}
	  
	public double getOutput() {
		return u;
	}
}