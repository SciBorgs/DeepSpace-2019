package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.logging.Logger.CommandStatus;

public class CloseArmCommand extends InstantCommand {
    private final String FILENAME = "CloseArmCommand.java";

    public CloseArmCommand(){ requires(Robot.intakeSubsystem) }

    @Override 
    protected void execute(){
	Robot.logger.logCommandStatus(FILENAME, CommandStatus.Executing);
        Robot.intakeSubsystem.closeArm();
    }
}
