/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.Robot;

public class ArmSubsystem extends Subsystem {
    public Talon m_motor;
    public final double LOWEST_ARM_POS = 0.0;
    public final double GREATEST_ARM_POS = Math.PI / 2;

    public ArmSubsystem(int motorChannel) {
        m_motor = new Talon(motorChannel);
    }

    public double getPos() {
        return Robot.getPigeonAngle();
    }

    public void setSpeed(double speed) {
        m_motor.set(speed);
    }

    @Override
    protected void initDefaultCommand() {

    }
}