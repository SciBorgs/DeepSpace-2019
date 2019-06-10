package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.logging.Logger.CommandStatus;
import edu.wpi.first.wpilibj.DigitalOutput;
import frc.robot.Utils; 

public class ToggleTargetLightCommand extends InstantCommand{
    private static final String FILENAME = "ToggleTargetLightCommand.java";
    private final DigitalOutput targetingLight = new DigitalOutput(5);

    public ToggleArmCommand() {}

    @Override protected void execute() {
		Robot.logger.logCommandStatus(FILENAME, CommandStatus.Executing);
        this.targetingLight.set(Utils.toggleDigitalOutput(targetingLight));
    }
}