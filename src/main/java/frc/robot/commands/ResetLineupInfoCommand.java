package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;

public class ResetLineupInfoCommand extends InstantCommand {
    private final String fileName = "ResetLineupInfoCommand.java";

    public ResetLineupInfoCommand() {}

    @Override protected void execute(){
        Robot.lineup.resetFound();
    }
}
