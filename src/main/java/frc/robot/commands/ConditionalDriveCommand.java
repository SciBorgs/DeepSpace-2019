/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team1155.robot.commands;

import org.usfirst.frc.team1155.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.ConditionalCommand;

public class ConditionalDriveCommand extends ConditionalCommand {
    private boolean condition = false;
    
    public ConditionalDriveCommand(Command onTrue, Command onFalse) {
        super(onTrue, onFalse);
        requires(Robot.driveSubsystem);
    }

    @Override
    protected boolean condition() {
        condition = !condition;
        return condition;
    }
}