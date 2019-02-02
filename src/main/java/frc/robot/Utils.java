package frc.robot;

import java.util.*;

public class Utils{
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
}