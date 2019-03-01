package frc.robot.helpers;

import edu.wpi.first.wpilibj.Timer;
import frc.robot.Utils;
import java.util.ArrayList;

public class PID {  

	Timer timer;
	private ArrayList<Double> times, errors;
	private int maxSize = 4;
	public double p, i, d, u, integral, deadband;
	    
	public PID(double p, double i, double d) {
		timer = new Timer();
		timer.start();
		times  = new ArrayList<Double>();
		errors = new ArrayList<Double>();
		this.p = p;
		this.i = i;
		this.d = d;
		deadband = .0001;
	}
	
	public ArrayList<Double> getErrors(){return errors;}
	public ArrayList<Double> getTimes() {return times;}
	public void reset() {
		u = 0;
		integral = 0;
		times = new ArrayList<Double>();
		errors = new ArrayList<Double>();
	}
	
	public void setP(double p) {this.p = p;}
	public void setI(double i) {this.i = i;}
	public void setD(double d) {this.d = d;}
	
	public void setSmoother(int amount) {maxSize = amount;}
	  
	public void add_measurement(double error) {
		add_measurement_d(error,errors.isEmpty() ? 0 : error - errors.get(0)); /// delta error is the dd if nothing is specified
	}
	  
	public void add_measurement_d(double error, double dd) {
		double currentTime = timer.get();
		if (!(errors.isEmpty())) {
		    double dt = currentTime - times.get(0);
		    double derivative = dd / dt;
		    integral += .5 * dt * (error + errors.get(0));
		    u = p * error + d * derivative + i * integral;
		}
		else
			u = p * error;
		Utils.trimAdd(times, currentTime, maxSize);
		Utils.trimAdd(errors, error, maxSize);
	}
	  
	public double getOutput() {return u;}
	public double getLimitOutput(double limit) {return Utils.limitOutput(u,limit);}
	public void setTolerance(double tolerance){deadband = tolerance;}
	public boolean targetReached(){
		return Math.abs(getOutput()) < deadband;
	}
}