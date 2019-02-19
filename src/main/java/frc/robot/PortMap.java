package frc.robot;

public class PortMap {

  	//*****************JOYSTICKS*****************//

    public static final int JOYSTICK_LEFT = 1;
    public static final int JOYSTICK_RIGHT = 0;

    public static final int JOYSTICK_TRIGGER = 1;
    public static final int JOYSTICK_CENTER_BUTTON = 2;
    public static final int JOYSTICK_LEFT_BUTTON = 3;
    public static final int JOYSTICK_RIGHT_BUTTON = 4;

    public static final int[][] JOYSTICK_BUTTON_MATRIX_LEFT = { { 5, 6, 7 }, { 10, 9, 8 } };
    public static final int[][] JOYSTICK_BUTTON_MATRIX_RIGHT = { { 13, 12, 11 }, { 14, 15, 16 } };

    //*******************SPARKS******************//

    public static final int LEFT_FRONT_SPARK = 6; 
    public static final int LEFT_MIDDLE_SPARK = 5; 
    public static final int LEFT_BACK_SPARK = 4;

    public static final int RIGHT_FRONT_SPARK = 3;
    public static final int RIGHT_MIDDLE_SPARK = 2;
    public static final int RIGHT_BACK_SPARK = 1;

    public static final int LIFT_SPARK = 10;

    //*******************TALONS******************//

    public static final int PIGEON_TALON = 9;

    public static final int LIFT_TALON = 6;
    public static final int ARM_TILT_TALON_LEFT = 7;
    public static final int ARM_TILT_TALON_RIGHT = 8;
    public static final int INTAKE_TALON = 10;

    //***************LIMIT*SWITCHES**************//

    public static final int BALL_LIMIT_SWITCH = 0;
    public static final int HATCH_LIMIT_SWITCH = 1;

    //***************DOUBLE*SOLENOIDS*************//

    public static final int[] GEAR_SHIFTER_SOLENOID = {0, 1};
    public static final int[] INTAKE_SOLENOID = {2, 3};

    public static final int LEFT_ZLIFT = 2;
    public static final int RIGHT_ZLIFT = 8;

    public static final int FORWARD_CHANNEL = 2;
    public static final int REVERSE_CHANNEL = 3;

    //*******************MISC********************//

    public static final int PRESSURE_SENSOR = 0;
}