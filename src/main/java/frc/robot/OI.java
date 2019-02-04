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
    public JoystickButton switchToRetroreflectiveButton, followBallButton;

    public OI() {
        rightStick = new Joystick(PortMap.JOYSTICK_RIGHT);
        leftStick = new Joystick(PortMap.JOYSTICK_LEFT);

        switchToRetroreflectiveButton = new JoystickButton(rightStick, PortMap.JOYSTICK_LEFT_BUTTON);
        switchToRetroreflectiveButton.whenPressed(new SwitchToRetroreflectiveCommand());
        switchToRetroreflectiveButton.whenReleased(new SwitchToCargoCommand());

        followBallButton = new JoystickButton(leftStick, PortMap.JOYSTICK_TRIGGER);
        followBallButton.whileHeld(new CargoFollowCommand());

    }
}