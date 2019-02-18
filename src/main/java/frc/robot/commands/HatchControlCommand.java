package frc.robot.commands;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.Robot;
import frc.robot.subsystems.IntakeSubsystem.HatchControl;

public class HatchControlCommand extends InstantCommand {

    private HatchControl control;

    public HatchControlCommand(HatchControl control) {
        requires(Robot.intakeSubsystem);
        this.control = control;
    }

    @Override protected void execute() {
        Robot.intakeSubsystem.updateHatchControl(control);
    }
}