package frc.robot.subsystems;

import frc.robot.Robot;
import frc.robot.PortMap;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.command.Subsystem;

public class GearShiftSubsystem extends Subsystem {

    public int countOfContinousCyclesAboveJoystickThreshold;
    public int countOfContinousCyclesBelowJoystickThreshold;
    public double joystickShiftUpThreshold = .5;
    public double joystickShiftDownThreshold = .2;
    public int cycleThreshold = 10; //50 Cycles is 1000 milliseconds diveided by 20ms per cycle
    
    private final double UPPER_HIGH_GEAR_THRESHOLD = 1000;
    private final double LOWER_LOW_GEAR_THRESHOLD = 500;

    public void updateCycleCount() {
        if (Math.abs((Robot.oi.leftStick.getY() - Robot.oi.rightStick.getY())/2.) >= joystickShiftUpThreshold) {
            countOfContinousCyclesAboveJoystickThreshold++;
            countOfContinousCyclesBelowJoystickThreshold = 0;
        } else if (Math.abs((Robot.oi.leftStick.getY() - Robot.oi.rightStick.getY())/2.) <= joystickShiftDownThreshold) {
            countOfContinousCyclesBelowJoystickThreshold++;
            countOfContinousCyclesAboveJoystickThreshold = 0;
        }
    }
    
    public void updateGearShift() {
        //System.out.println(countOfContinousCyclesAboveJoystickThreshold);
        //System.out.println(countOfContinousCyclesBelowJoystickThreshold);
        if (countOfContinousCyclesAboveJoystickThreshold >= cycleThreshold) {shiftUp();}
        if (countOfContinousCyclesBelowJoystickThreshold >= cycleThreshold) {shiftDown();}
    }

    public boolean currentlyInHighGear(){
        return gearShiftSolenoid.get() == DoubleSolenoid.Value.kForward;
    }

    public boolean currentlyInLowGear(){
        return gearShiftSolenoid.get() == DoubleSolenoid.Value.kReverse;
    }

    public void shiftGear() {
        //updateCycleCount();
        //updateGearShift();
        if(gearShiftSolenoid.get() == DoubleSolenoid.Value.kReverse){
            gearShiftSolenoid.set(DoubleSolenoid.Value.kForward);
        }else{
            gearShiftSolenoid.set(DoubleSolenoid.Value.kReverse);
        }
    }

    public void autoShift(){
        double speed = Math.min(Robot.driveSubsystem.lf.getEncoder().getVelocity(), Robot.driveSubsystem.rf.getEncoder().getVelocity());
        if(currentlyInHighGear()){
            if(speed > UPPER_HIGH_GEAR_THRESHOLD){
                shiftDown();
            }
        }
        if(currentlyInLowGear()){
            if(speed < LOWER_LOW_GEAR_THRESHOLD){
                shiftUp();
            }
        }
    }

    public DoubleSolenoid gearShiftSolenoid;
    public void shiftUp() {
        System.out.println(gearShiftSolenoid.get() != DoubleSolenoid.Value.kForward);
        if (gearShiftSolenoid.get() != DoubleSolenoid.Value.kForward) {
            gearShiftSolenoid.set(DoubleSolenoid.Value.kForward);
            //System.out.println("Shifted up");
        }
        //System.out.println("Shifted up done");
    }
    public void shiftDown() {
        System.out.println(gearShiftSolenoid.get() != DoubleSolenoid.Value.kReverse);
        if (gearShiftSolenoid.get() != DoubleSolenoid.Value.kReverse) {
            gearShiftSolenoid.set(DoubleSolenoid.Value.kReverse);
            //System.out.println("Shifted down");
        }
        //System.out.println("Shifted down done");
    }

    public GearShiftSubsystem() {
        gearShiftSolenoid = new DoubleSolenoid(PortMap.GEAR_SHIFTER_SOLENOID[0], PortMap.GEAR_SHIFTER_SOLENOID[1]);
        // countOfContinousCyclesAboveJoystickThreshold = 0;
        // countOfContinousCyclesBelowJoystickThreshold = 0;
        System.out.println("shifting>>>");
        gearShiftSolenoid.set(DoubleSolenoid.Value.kReverse);
    }

    @Override
    protected void initDefaultCommand() {
        // LITTERALLY DIE
    }
}
