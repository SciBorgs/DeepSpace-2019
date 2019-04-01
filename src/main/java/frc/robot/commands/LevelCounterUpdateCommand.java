package frc.robot.commands;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.Robot;
import frc.robot.logging.Logger.DefaultValue;

public class LevelCounterUpdateCommand extends InstantCommand {
    private final String fileName = "LevelCounterUpdateCommand.java";
    public enum LevelChange {Up, Down}
    private int change;

    public LevelCounterUpdateCommand(LevelChange change) {
        this.change = change == LevelChange.Up ? 1 : -1;
    }

    @Override protected void execute() {
        Robot.logger.addData(this.fileName, Robot.logger.commandStatus, "executing", DefaultValue.Empty);
        Robot.liftSubsystem.moveLevelCounter(change);
        Robot.liftSubsystem.updateLevelCounterWidget();
    }
}
