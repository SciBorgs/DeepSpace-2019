package frc.robot.commands;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.robot.Robot;

public class ConditionalSuckCommand extends ConditionalCommand {

    public ConditionalSuckCommand() {
        super(new SuckCommand(), new CargoFollowCommand());
        requires(Robot.liftSubsystem);
    }

    @Override protected boolean condition() {
        return !Robot.intakeSubsystem.holdingGamePiece();
    }
}