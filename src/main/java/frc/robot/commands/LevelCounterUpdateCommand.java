package frc.robot.commands;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.Robot;
import frc.robot.logging.Logger.CommandStatus;
import frc.robot.logging.Logger.DefaultValue;

public class LevelCounterUpdateCommand extends InstantCommand {
    private final String fileName = "LevelCounterUpdateCommand.java";
    public enum LevelChange {Up, Down}
    private int change;

    public LevelCounterUpdateCommand(LevelChange change) {
        this.change = change == LevelChange.Up ? 1 : -1;
    }

    @Override protected void execute() {
        System.out.println("updating set point");
		Robot.logger.logCommandStatus(this.fileName, CommandStatus.Executing);
        Robot.liftSubsystem.moveLevelCounter(change);
        Robot.liftSubsystem.updateLevelCounterWidget();
        //(new LiftCommand()).start();
    }
}
