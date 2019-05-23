package frc.robot.helpers;

import edu.wpi.first.wpilibj.Timer;
import frc.robot.Utils;
import java.util.ArrayList;

public class PID {  

	Timer timer;
	private ArrayList<Double> times, errors;
	private int maxSize = 4;
	private double p, i, d, u, integral, negligibleOutput;

	public PID(double p, double i, double d) {
		this.timer = new Timer();
		this.timer.start();
		this.times  = new ArrayList<Double>();
		this.errors = new ArrayList<Double>();
		this.p = p;
		this.i = i;
		this.d = d;
		this.negligibleOutput = 0;
	}
	
	public ArrayList<Double> getErrors(){return this.errors;}
	public ArrayList<Double> getTimes() {return this.times;}
	public void reset() {
		this.u = 0;
		this.integral = 0;
		this.times = new ArrayList<Double>();
		this.errors = new ArrayList<Double>();
	}
	
	public void setP(double p) {this.p = p;}
	public void setI(double i) {this.i = i;}
	public void setD(double d) {this.d = d;}
	
	public double getP() {return this.p;}
	public double getI() {return this.i;}
	public double getD() {return this.d;}
	
	public void setSmoother(int maxSize) {this.maxSize = maxSize;}
	  
	public void addMeasurement(double error) {
		double dd_dt = 0;
		if (!this.errors.isEmpty()) {
			double dd = error - this.errors.get(0);
			double dt = this.timer.get() - this.times.get(0);
			dd_dt = dd / dt;
		}
		addMeasurementWithDerivative(error, dd_dt);
	}
	  
	public void addMeasurementWithDerivative(double error, double derivative) {
		// This is split up into two functions because often we will have a better estimate of the derivative of the error
		// The split up allows us to use that better estimate by calling this step directly
		double currentTime = timer.get();
		this.integral += error;
		this.u = this.p * error + this.d * derivative + this.i * this.integral;
		Utils.trimAdd(this.times, currentTime, this.maxSize);
		Utils.trimAdd(this.errors, error, this.maxSize);
	}
	  
	public double getOutput() {return this.u;}
	public double getLimitedOutput(double limit) {return Utils.limitOutput(u,limit);}
	public void setNegligibleOutput(double tolerance){this.negligibleOutput = tolerance;}
	public boolean outputIsNegligible(){
		return Math.abs(getOutput()) < this.negligibleOutput;
	}
}