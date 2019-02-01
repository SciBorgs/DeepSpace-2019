package frc.robot;

public class PortMap {

  	// *****************JOYSTICKS*****************//

	public static final int JOYSTICK_LEFT = 1;
	public static final int JOYSTICK_RIGHT = 0;

	public static final int JOYSTICK_TRIGGER = 1;
	public static final int JOYSTICK_CENTER_BUTTON = 2;
	public static final int JOYSTICK_LEFT_BUTTON = 3;
	public static final int JOYSTICK_RIGHT_BUTTON = 4;

	public static final int[][] JOYSTICK_BUTTON_MATRIX_LEFT = { { 5, 6, 7 }, { 10, 9, 8 } };

	public static final int[][] JOYSTICK_BUTTON_MATRIX_RIGHT = { { 13, 12, 11 }, { 14, 15, 16 } };

    public static final int LEFT_FRONT_TALON = 6; 
    public static final int LEFT_MIDDLE_TALON = 7; 
    public static final int LEFT_BACK_TALON = 8;

    public static final int RIGHT_FRONT_TALON = 16;
    public static final int RIGHT_MIDDLE_TALON = 15;
    public static final int RIGHT_BACK_TALON = 14;
    
    public static final int PIGEON_TALON = 2;

    // Temporary arm talon port
    public static final int ARM_TALON = 10;
}