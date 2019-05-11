package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.robot.commands.*;
import frc.robot.commands.LevelCounterUpdateCommand.LevelChange;

// FILE HAS NOT BEEN CLEANED UP //
public class OI {
    public Joystick leftStick, rightStick;
    public JoystickButton gearShiftButton, cargoFollowButton, lineupButton, startZLiftButton, liftLevelUpButton, liftLevelDownButton, suckButton, spitButton, depositPanelButton, hatchSecureModeButton, armModeButton, climbFrontButton, climbBackButton;
    public XboxController xboxController;

    public OI() {
        System.out.println("OI constructor");
        leftStick = new Joystick(PortMap.JOYSTICK_LEFT);
        rightStick = new Joystick(PortMap.JOYSTICK_RIGHT);
        xboxController = new XboxController(PortMap.XBOX_CONTROLLER);

        
        // Left Stick
        lineupButton = new JoystickButton(rightStick, PortMap.JOYSTICK_LEFT_BUTTON);
        lineupButton.whenPressed(new LineupCommand());

        liftLevelUpButton = new JoystickButton(leftStick, PortMap.JOYSTICK_CENTER_BUTTON);
        liftLevelUpButton.whenPressed(new LevelCounterUpdateCommand(LevelChange.Up));
        //liftLevelUpButton.whenPressed(new LiftCommand());

        liftLevelDownButton = new JoystickButton(leftStick, PortMap.JOYSTICK_TRIGGER);
        cargoFollowButton = liftLevelDownButton; // Only works if we are at the lowest level
        liftLevelDownButton.whenPressed(new LevelCounterUpdateCommand(LevelChange.Down));
        //liftLevelDownButton.whenPressed(new LiftCommand());

        // Right Stick
        //startZLiftButton = new JoystickButton(rightStick, PortMap.JOYSTICK_LEFT_BUTTON);
        //startZLiftButton.whenPressed(new ZLiftCommand(leftStick, rightStick));

        suckButton = new JoystickButton(leftStick, PortMap.JOYSTICK_LEFT_BUTTON);
        suckButton.whenPressed(new CargoFollowCommand());
        suckButton.whenReleased(new CloseArmCommand());
        suckButton.whenReleased(new SecureCargoCommand());
        
        spitButton = new JoystickButton(leftStick, PortMap.JOYSTICK_RIGHT_BUTTON);
        spitButton.whenPressed(new CargoReleaseCommand());
        spitButton.whenReleased(new StopIntakeCommand());

        hatchSecureModeButton = new JoystickButton(rightStick, PortMap.JOYSTICK_TRIGGER);
        hatchSecureModeButton.whenPressed(new ReleaseHatchCommand());
        hatchSecureModeButton.whenReleased(new SecureHatchCommand());
        hatchSecureModeButton.whenPressed(new PopHatchCommand());

        armModeButton = new JoystickButton(rightStick, PortMap.JOYSTICK_CENTER_BUTTON);
        armModeButton.whenPressed(new ToggleArmCommand());

        gearShiftButton = new JoystickButton(rightStick, PortMap.JOYSTICK_RIGHT_BUTTON);
        gearShiftButton.whileHeld(new GearShiftCommand());

        // Xbox 
        
        climbFrontButton = new JoystickButton(xboxController, PortMap.XBOX_BUMPER_RIGHT);
        climbFrontButton.whenPressed(new ClimbToggleFrontCommand());

        climbBackButton = new JoystickButton(xboxController, PortMap.XBOX_BUMPER_LEFT);
        climbBackButton.whenPressed(new ClimbToggleBackCommand());
        

        climbFrontButton = new JoystickButton(leftStick, PortMap.JOYSTICK_BUTTON_MATRIX_RIGHT[0][0]);
        climbFrontButton.whenPressed(new ClimbToggleFrontCommand());

        climbBackButton = new JoystickButton(rightStick, PortMap.JOYSTICK_BUTTON_MATRIX_LEFT[0][0]);
        climbBackButton.whenPressed(new ClimbToggleBackCommand());


    }
}
