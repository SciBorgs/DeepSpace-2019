/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.PID;
import frc.robot.Robot;

public class JoystickArmCommand extends Command {
    private PID pid;
    private double m_speed;

    public JoystickArmCommand(double speed) {
        requires(Robot.armSubsystem);

        m_speed = speed;
    }

    @Override
    protected void initialize() {
        Robot.armSubsystem.setSpeed(0);
        pid = new PID(Robot.ARM_P_CONSTANT, 0, Robot.ARM_D_CONSTANT);
    }

    @Override
    protected void execute() {
        pid.add_measurement(m_speed - Robot.armSubsystem.m_motor.getSpeed());
        Robot.armSubsystem.setSpeed(pid.getOutput());
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

    @Override
    protected void end() {
        Robot.armSubsystem.setSpeed(0);
    }

    @Override
    protected void interrupted() {
        end();
    }
}