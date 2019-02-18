package frc.robot.commands;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.Robot;

public class LevelCounterUpdate extends InstantCommand {

    private int change; 

    public LevelCounterUpdate(boolean val) {
        change = val ? 1 : -1;
    }

    @Override protected void execute() {
        Robot.liftSubsystem.updateLevelCounter(change);
        Robot.liftSubsystem.updateLevelCounterWidget();
    }
}
