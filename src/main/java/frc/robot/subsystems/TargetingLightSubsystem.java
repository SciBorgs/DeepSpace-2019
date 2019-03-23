package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.DigitalOutput;
/**
 * Add your docs here.
 */
public class TargetingLightSubsystem extends Subsystem{ 
 
    private DigitalOutput targetingLight;

    public TargetingLightSubsystem(){   
        targetingLight = new DigitalOutput(5);
    }

    public void turnOn(){
        targetingLight.set(true);
    }

    public void turnOff(){
        targetingLight.set(false);
    }


    @Override
    protected void initDefaultCommand() {
      // LITTERALLY DIE
    }
}