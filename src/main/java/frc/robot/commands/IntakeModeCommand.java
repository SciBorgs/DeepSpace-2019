package frc.robot.commands;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.Robot;
import frc.robot.subsystems.IntakeSubsystem.IntakeMode;

public class IntakeModeCommand extends InstantCommand {

    private IntakeMode mode;
    public IntakeModeCommand(IntakeMode mode) {
        requires(Robot.intakeSubsystem);
        this.mode = mode;
    }

    @Override protected void execute() {
        Robot.intakeSubsystem.updateIntakeMode(mode);
    }
}