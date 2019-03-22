package frc.robot.controlscheme;

import edu.wpi.first.wpilibj.buttons.Trigger;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Implementation copied from edu.wpi.first.wpilibj.buttons.Button.
 * The only difference is that it's based on ButtonActions instead of Commands.
 */
public abstract class ControlButton extends ControlTrigger {
    /**
     * Starts the given command whenever the button is newly pressed.
     *
     * @param command the command to start
     */
    public void whenPressed(final Command command) {
        whenActive(command);
    }

    public void whenPressed(final ButtonAction action) {
        whenActive(action);
    }

    /**
     * Constantly starts the given command while the button is held.
     *
     * {@link Command#start()} will be called repeatedly while the button is held, and will be
     * canceled when the button is released.
     *
     * @param command the command to start
     */
    public void whileHeld(final Command command) {
        whileActive(command);
    }

    public void whileHeld(final ButtonAction action) {
        whileActive(action);
    }

    /**
     * Starts the command when the button is released.
     *
     * @param command the command to start
     */
    public void whenReleased(final Command command) {
        whenInactive(command);
    }

    public void whenReleased(final ButtonAction action) {
        whenInactive(action);
    }

    /**
     * Toggles the command whenever the button is pressed (on then off then on).
     *
     * @param command the command to start
     */
    public void toggleWhenPressed(final Command command) {
        toggleWhenActive(command);
    }

    /**
     * Cancel the command when the button is pressed.
     *
     * @param command the command to start
     */
    public void cancelWhenPressed(final Command command) {
        cancelWhenActive(command);
    }
}

/**
 * Implementation copied from edu.wpi.first.wpilibj.buttons.Trigger.
 * The only difference is that it's based on ButtonActions instead of Commands.
 */
abstract class ControlTrigger extends Trigger {
    private boolean grab() {
        return get();
    }

    /**
     * Starts the given command whenever the trigger just becomes active.
     *
     * @param action the action to perform
     */
    public void whenActive(final ButtonAction action) {
        new ButtonScheduler() {
            private boolean m_pressedLast = grab();

            @Override
            public void execute() {
                boolean pressed = grab();

                if (!m_pressedLast && pressed) {
                    action.act();
                }

                m_pressedLast = pressed;
            }
        }.start();
    }

    /**
     * Constantly starts the given command while the button is held.
     *
     * {@link ButtonAction#act()} ()} will be called repeatedly while the trigger is active.
     *
     * @param action the action to perform
     */
    public void whileActive(final ButtonAction action) {
        new ButtonScheduler() {
            private boolean m_pressedLast = grab();

            @Override
            public void execute() {
                boolean pressed = grab();

                if (pressed) {
                    action.act();
                }

                m_pressedLast = pressed;
            }
        }.start();
    }

    /**
     * Starts the command when the trigger becomes inactive.
     *
     * @param action the action to perform
     */
    public void whenInactive(final ButtonAction action) {
        new ButtonScheduler() {
            private boolean m_pressedLast = grab();

            @Override
            public void execute() {
                boolean pressed = grab();

                if (m_pressedLast && !pressed) {
                    action.act();
                }

                m_pressedLast = pressed;
            }
        }.start();
    }
}
