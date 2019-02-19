package frc.robot.commands;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.Robot;
import frc.robot.subsystems.IntakeSubsystem.HatchDepositControl;

public class HatchControlCommand extends InstantCommand {

    private HatchDepositControl control;

    public HatchControlCommand(HatchDepositControl control) {
        requires(Robot.intakeSubsystem);
        this.control = control;
    }

    @Override protected void execute() {
        Robot.intakeSubsystem.updateHatchControl(control);
    }
}