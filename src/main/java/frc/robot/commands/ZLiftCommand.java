package frc.robot.commands;

import frc.robot.*;
import edu.wpi.first.wpilibj.command.Command;

public class ZLiftCommand extends Command {
    private boolean m_stop;

    public ZLiftCommand(boolean stop) {
        requires(Robot.zLiftSubsystem);

        m_stop = stop;
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
        return m_stop;
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