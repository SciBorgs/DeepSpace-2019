package frc.robot.controlscheme;

import edu.wpi.first.wpilibj.XboxController;

/**
 * A {@link ControlButton} that gets it's state from an {@link XboxController}
 */
public class XboxControlButton extends ControlButton {
    private final XboxController xboxController;
    private final Button button;

    /**
     * Represents a digital button on an XboxController.
     * <p>
     * Values from {@link XboxController} since it's private.
     */
    public enum Button {
        BUMPER_LEFT(5),
        BUMPER_RIGHT(6),
        STICK_LEFT(9),
        STICK_RIGHT(10),
        A(1),
        B(2),
        X(3),
        Y(4),
        BACK(7),
        START(8);

        @SuppressWarnings({"MemberName", "PMD.SingularField"})
        private final int value;

        Button(int value) {
            this.value = value;
        }
    }

    public XboxControlButton(XboxController xboxController, Button button) {
        this.xboxController = xboxController;
        this.button = button;
    }

    @Override
    public boolean get() {
        return xboxController.getRawButton(button.value);
    }
}
