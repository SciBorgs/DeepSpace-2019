package frc.robot.commands;

import frc.robot.*;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

public class ZLiftCommand extends Command {
    private JoystickButton control;

    public ZLiftCommand(JoystickButton control) {
        requires(Robot.zLiftSubsystem);

        // m_stop tells ZLiftCommand whether to stop.
        // m_stop is true when the button used for calling
        // ZLiftCommand is released.
        this.control = control;
    }

    @Override
    protected void initialize() {
        Robot.zLiftSubsystem.reset();
        Robot.zLiftSubsystem.unlockPistons();
    }

    @Override
    protected void execute() {
        Robot.zLiftSubsystem.lift();
    }

    @Override
    protected boolean isFinished() {
        return !control.get();
    }

    @Override
    protected void end() {
        Robot.zLiftSubsystem.reset();
    }

    @Override
    protected void interrupted() {
        end();
    }
}
