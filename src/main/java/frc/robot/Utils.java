package frc.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;

import java.util.*;

public class Utils{

    public static double METERS_TO_INCHES = 39.37;

    public static double metersToInches(double meters){
        return meters * METERS_TO_INCHES;
    }
    public static double inchesToMeters(double inches){
        return inches / METERS_TO_INCHES;
    }

    public static double last(ArrayList<Double> arr) {
        return arr.get(arr.size() - 1);
    }

    public static void trimIf(ArrayList<Double> arr, int maxSize) {
        // Trims an array down to a max size, starting from the start
        while (maxSize < arr.size())
            arr.remove(0);
    }

    public static void trimAdd(ArrayList<Double> arr, double val, int maxSize) {
        // Adds a value to the array and then trims it to a max size
        arr.add(val);
        trimIf(arr, maxSize);
    }

    public static double limitOutput(double output, double max){
        if (output > max)
            return max;
        else if (output < - max)
            return -max;
        else
            return output;
    }

    public static void setTalon(TalonSRX talon, double speed){
        talon.set(ControlMode.PercentOutput, speed);
    }

    public static int boolToInt(boolean b){
        if (b) {
            return 1;
        } else {
            return 0;
        }
    }

    public static HashSet<String> arrayListToHashset(ArrayList<String> values){
        HashSet<String> end = new HashSet<String>();
        for (String val : values){
            end.add(val);
        }
        return end;
    }
}