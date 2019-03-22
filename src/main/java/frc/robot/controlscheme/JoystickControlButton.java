package frc.robot.controlscheme;

import edu.wpi.first.wpilibj.Joystick;

public class JoystickControlButton extends ControlButton {
    private final Joystick joystick;
    private final Button button;

    public enum Button {
        TRIGGER(1),
        CENTER(2),
        LEFT(3),
        RIGHT(4),
        MATRIX_LEFT_TOP_LEFT(5),
        MATRIX_LEFT_TOP_CENTER(6),
        MATRIX_LEFT_TOP_RIGHT(7),
        MATRIX_LEFT_BOTTOM_LEFT(10),
        MATRIX_LEFT_BOTTOM_CENTER(9),
        MATRIX_LEFT_BOTTOM_RIGHT(8),
        MATRIX_RIGHT_TOP_LEFT(13),
        MATRIX_RIGHT_TOP_CENTER(12),
        MATRIX_RIGHT_TOP_RIGHT(11),
        MATRIX_RIGHT_BOTTOM_LEFT(14),
        MATRIX_RIGHT_BOTTOM_CENTER(15),
        MATRIX_RIGHT_BOTTOM_RIGHT(16);

        private final int value;
        Button(int value) {
            this.value = value;
        }
    }

    public JoystickControlButton(Joystick joystick, Button button) {
        this.joystick = joystick;
        this.button = button;
    }

    @Override
    public boolean get() {
        return joystick.getRawButton(button.value);
    }
}
