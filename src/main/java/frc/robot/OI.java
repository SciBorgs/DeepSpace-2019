package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.robot.commands.*;
import frc.robot.subsystems.LiftSubsystem.Target;

public class OI {
    public Joystick rightStick, leftStick;
    public JoystickButton switchToRetroreflectiveButton, followBallButton, lineupButton, startZLift, liftLow, liftMid, liftHigh, suckButton, spitButton;

    public OI() {
        rightStick = new Joystick(PortMap.JOYSTICK_RIGHT);
        leftStick = new Joystick(PortMap.JOYSTICK_LEFT);

        suckButton = new JoystickButton(rightStick, PortMap.JOYSTICK_CENTER_BUTTON);
        suckButton.whenPressed(new ConditionalSuckCommand());
        
        spitButton = new JoystickButton(rightStick, PortMap.JOYSTICK_RIGHT_BUTTON);

        //switchToRetroreflectiveButton = new JoystickButton(rightStick, PortMap.JOYSTICK_RIGHT_BUTTON);
        //switchToRetroreflectiveButton.whenPressed(new SwitchToRetroreflectiveCommand());
        //switchToRetroreflectiveButton.whenReleased(new SwitchToCargoCommand());

        lineupButton = new JoystickButton(leftStick, PortMap.JOYSTICK_TRIGGER);
        //lineupButton.whenPressed(new ResetLineupInfoCommand());
        lineupButton.whileHeld(new LineupCommand());

        startZLift = new JoystickButton(rightStick, PortMap.JOYSTICK_CENTER_BUTTON);
        //startZLift.whenPressed(new ZLiftCommand(false));
        //startZLift.whenReleased(new ZLiftCommand(true));

        liftLow = new JoystickButton(rightStick, PortMap.JOYSTICK_BUTTON_MATRIX_LEFT[0][0]);
        liftMid = new JoystickButton(rightStick, PortMap.JOYSTICK_BUTTON_MATRIX_LEFT[0][1]);
        liftHigh = new JoystickButton(rightStick, PortMap.JOYSTICK_BUTTON_MATRIX_LEFT[0][2]);
        liftLow.whenPressed(new LiftCommand(Target.Low));
        liftMid.whenPressed(new LiftCommand(Target.Mid));
        liftHigh.whenPressed(new LiftCommand(Target.High));

    }
}