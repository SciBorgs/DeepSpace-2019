package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.logging.Logger.DefaultValue;


public class ToggleArmCommand extends InstantCommand {
    private final String fileName = "ToggleArmCommand.java";

    public ToggleArmCommand(){}

    @Override protected void execute(){
        Robot.logger.addData(this.fileName, Robot.logger.commandStatus, "executing", DefaultValue.Empty);
        Robot.intakeSubsystem.toggleArm();
        if (Robot.intakeSubsystem.isArmOpen()){
            Robot.intakeSubsystem.updateHoldingHatch(true);
        }
    }
}