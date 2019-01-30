/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package frc.robot.subsystems;
import frc.robot.*;

import edu.wpi.first.wpilibj.command.Subsystem;

public class LiftSubsystem extends Subsystem {
    public final double LIFT_P_GAIN = 0.1;
    public final double LIFT_I_GAIN = 0.0;
    public final double LIFT_D_GAIN = 0.0;

    // TOOD: Maybe change the motor? xD
    public double getPos() {
        return Robot.rb.getEncoder().getPosition();
    }

    public void setSpeed(double speed) {
        Robot.rb.set(speed);
    }

    @Override
    protected void initDefaultCommand() {
    }
}