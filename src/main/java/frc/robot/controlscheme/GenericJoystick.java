package frc.robot.controlscheme;

/**
 * A base joystick interface without the requirements and clutter of the {@link edu.wpi.first.wpilibj.Joystick} class
 */
public interface GenericJoystick {
    /**
     * @return the x value of the joystick
     */
    double getX();

    /**
     * @return the y value of the joystick
     */
    double getY();
}
