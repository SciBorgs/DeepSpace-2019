package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;

public class SecureCargoCommand extends InstantCommand {
    private final String fileName = "SecureCargoCommand.java";

    public SecureCargoCommand(){}

    @Override protected void execute(){
        Robot.intakeSubsystem.secureCargo();
    }
}