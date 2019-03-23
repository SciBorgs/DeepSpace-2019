package frc.robot.commands;

import frc.robot.Robot;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import edu.wpi.first.wpilibj.command.InstantCommand;


import edu.wpi.first.wpilibj.command.Command;

public class GearShiftCommand extends InstantCommand {
	public GearShiftCommand() {
        requires(Robot.gearShiftSubsystem);
	}

	@Override protected void execute(){
		Robot.gearShiftSubsystem.shiftGear();
	}
}