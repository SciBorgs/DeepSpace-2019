/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.robot.commands.*;

public class OI {
    public Joystick rightStick, leftStick;
    public JoystickButton switchToRetroreflectiveButton, followBallButton, lineupButton, startZLift;

    public OI() {
        rightStick = new Joystick(PortMap.JOYSTICK_RIGHT);
        leftStick = new Joystick(PortMap.JOYSTICK_LEFT);

        switchToRetroreflectiveButton = new JoystickButton(rightStick, PortMap.JOYSTICK_TOGGLE_OBJECTIVE);
        switchToRetroreflectiveButton.whenPressed(new SwitchToRetroreflectiveCommand());
        switchToRetroreflectiveButton.whenReleased(new SwitchToCargoCommand());
        
        followBallButton = new JoystickButton(leftStick, PortMap.JOYSTICK_FOLLOW_CARGO_BUTTON);
        followBallButton.whileHeld(new CargoFollowCommand());

        lineupButton = new JoystickButton(leftStick, PortMap.JOYSTICK_LINEUP_BUTTON);
        lineupButton.whenPressed(new LineupCommand());

        startZLift = new JoystickButton(rightStick, PortMap.JOYSTICK_Z_LIFT_BUTTON);
        startZLift.whenPressed(new ZLiftCommand(false));
        startZLift.whenReleased(new ZLiftCommand(true));
    }
}