package frc.robot.commands;

import frc.robot.*;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.Joystick;

public class ZLiftCommand extends Command {
    private Joystick zLiftControl;
    private Joystick speedControl;

    public ZLiftCommand() {
        requires(Robot.zLiftSubsystem);

        // m_stop tells ZLiftCommand whether to stop.
        // m_stop is true when the button used for calling
        // ZLiftCommand is released.
        zLiftControl = Robot.oi.rightStick;
        speedControl = Robot.oi.leftStick;
    }

    @Override
    protected void initialize() {
        Robot.zLiftSubsystem.reset();
        Robot.zLiftSubsystem.unlockPistons();
    }

    @Override
    protected void execute() {
        Robot.zLiftSubsystem.lift(Robot.driveSubsystem.processStick(zLiftControl));
        Robot.driveSubsystem.setSpeed(speedControl, speedControl);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

    @Override
    protected void end() {
        Robot.zLiftSubsystem.reset();
        Robot.driveSubsystem.setSpeedTank(0,0);;
    }

    @Override
    protected void interrupted() {
        end();
    }
}
