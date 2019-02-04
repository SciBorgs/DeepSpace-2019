package frc.robot;

public class PortMap {

  	//*****************JOYSTICKS*****************//

	public static final int JOYSTICK_LEFT = 0;
	public static final int JOYSTICK_RIGHT = 1;

	public static final int JOYSTICK_TRIGGER = 1;
	public static final int JOYSTICK_CENTER_BUTTON = 2;
	public static final int JOYSTICK_LEFT_BUTTON = 3;
	public static final int JOYSTICK_RIGHT_BUTTON = 4;

	public static final int[][] JOYSTICK_BUTTON_MATRIX_LEFT = { { 5, 6, 7 }, { 10, 9, 8 } };

	public static final int[][] JOYSTICK_BUTTON_MATRIX_RIGHT = { { 13, 12, 11 }, { 14, 15, 16 } };

    //*******************SPARKS******************//

    public static final int LEFT_FRONT_SPARK = 0; 
    public static final int LEFT_MIDDLE_SPARK = 1; 
    public static final int LEFT_BACK_SPARK = 2;

    public static final int RIGHT_FRONT_SPARK = 3;
    public static final int RIGHT_MIDDLE_SPARK = 4;
    public static final int RIGHT_BACK_SPARK = 5;

    //***************LIMIT*SWITCHES**************//

    public static final int BALL_LIMIT_SWITCH = 0;
    public static final int HATCH_LIMIT_SWITCH = 1;

    //***************DOUBLE*SOLENOIDS*************//

    public static final int[] GEAR_SHIFTER_SOLENOID = {0, 1};

    public static final int PIGEON_TALON = 6;
}