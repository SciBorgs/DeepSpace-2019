package frc.robot;

public class PortMap {

  	//*****************JOYSTICKS*****************//

	public static final int JOYSTICK_LEFT = 0;
	public static final int JOYSTICK_RIGHT = 1;

	public static final int JOYSTICK_LINEUP_BUTTON = 1;
	public static final int JOYSTICK_Z_LIFT_BUTTON = 2;
	public static final int JOYSTICK_FOLLOW_CARGO_BUTTON = 3;
	public static final int JOYSTICK_TOGGLE_OBJECTIVE= 4;

	public static final int[][] JOYSTICK_BUTTON_MATRIX_LEFT = { { 5, 6, 7 }, { 10, 9, 8 } };

	public static final int[][] JOYSTICK_BUTTON_MATRIX_RIGHT = { { 13, 12, 11 }, { 14, 15, 16 } };

    //*******************SPARKS******************//

    public static final int LEFT_FRONT_SPARK = 6; 
    public static final int LEFT_MIDDLE_SPARK = 7; 
    public static final int LEFT_BACK_SPARK = 8;

    public static final int RIGHT_FRONT_SPARK = 14;
    public static final int RIGHT_MIDDLE_SPARK = 15;
    public static final int RIGHT_BACK_SPARK = 16;

    //***************LIMIT*SWITCHES**************//

    public static final int BALL_LIMIT_SWITCH = 0;
    public static final int HATCH_LIMIT_SWITCH = 1;

    //***************DOUBLE*SOLENOIDS*************//

    public static final int[] GEAR_SHIFTER_SOLENOID = {0, 1};

    public static final int PIGEON_TALON = 6;

    public static final int LEFT_ZLIFT = 2;
    public static final int RIGHT_ZLIFT = 8;

    public static final int FORWARD_CHANNEL = 2;
    public static final int REVERSE_CHANNEL = 3;
}