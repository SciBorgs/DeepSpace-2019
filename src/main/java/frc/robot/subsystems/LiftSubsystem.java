package frc.robot.subsystems;
import frc.robot.*;

import edu.wpi.first.wpilibj.command.Subsystem;

public class LiftSubsystem extends Subsystem {
    public final double LIFT_P_GAIN = 0.1;
    public final double LIFT_I_GAIN = 0.0;
    public final double LIFT_D_GAIN = 0.0;

    // TOOD: Maybe change the motor? xD
    public double getPos() {
        //return Robot.driveSubsystem.rb.getEncoder().getPosition();
        return 0;
    }

    public void setSpeed(double speed) {
        //Robot.driveSubsystem.rb.set(speed);
    }


    @Override
    protected void initDefaultCommand() {}
}