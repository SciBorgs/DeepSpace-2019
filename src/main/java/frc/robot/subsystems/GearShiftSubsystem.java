package frc.robot.subsystems;

import frc.robot.Robot;
import frc.robot.Utils;
import frc.robot.logging.Logger.DefaultValue;
import frc.robot.PortMap;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.command.Subsystem;

public class GearShiftSubsystem extends Subsystem {

	private final String fileName = "GearShiftSubsystem.java";
    public DoubleSolenoid gearShiftSolenoid;
    
    private final double UPPER_HIGH_GEAR_THRESHOLD = 1000;
    private final double LOWER_LOW_GEAR_THRESHOLD = 500;
	public static final DoubleSolenoid.Value HIGH_GEAR_VALUE = Value.kForward;
	public static final DoubleSolenoid.Value LOW_GEAR_VALUE = Utils.oppositeDoubleSolenoidValue(HIGH_GEAR_VALUE);

    public GearShiftSubsystem() {
        this.gearShiftSolenoid = Utils.newDoubleSolenoid(PortMap.GEAR_SHIFTER_SOLENOID_PDP, PortMap.GEAR_SHIFTER_SOLENOID);
        shiftUp();
    }
    
	public void periodicLog(){
        String gear = currentlyInHighGear() ? "high" : "low";
        Robot.logger.addData(this.fileName, "gear", gear, DefaultValue.Previous);
	}
    
    public void autoShift(){
        double speed = Robot.robotPosition.getSpeed();
        if(speed > UPPER_HIGH_GEAR_THRESHOLD){shiftDown();}
        if(speed < LOWER_LOW_GEAR_THRESHOLD) {shiftUp();}
    }

    public boolean currentlyInHighGear(){return this.gearShiftSolenoid.get() == HIGH_GEAR_VALUE;}
    public boolean currentlyInLowGear() {return this.gearShiftSolenoid.get() == LOW_GEAR_VALUE;}

    public void shiftUp()  {this.gearShiftSolenoid.set(HIGH_GEAR_VALUE);}
    public void shiftDown(){this.gearShiftSolenoid.set(LOW_GEAR_VALUE);}

    public void toggleGear() {Utils.toggleDoubleSolenoid(this.gearShiftSolenoid);}

    @Override
    protected void initDefaultCommand() {
        // LITTERALLY DIE
    }
}
