package frc.robot.commands;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.Robot;

public class LevelCounterUpdateCommand extends InstantCommand {
    public enum LevelChange {Up, Down}
    private int change;

    public LevelCounterUpdateCommand(LevelChange change) {
        this.change = change == LevelChange.Up ? 1 : -1;
    }

    @Override protected void execute() {
        Robot.liftSubsystem.moveLevelCounter(change);
        Robot.liftSubsystem.updateLevelCounterWidget();
    }
}
