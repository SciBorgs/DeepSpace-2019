package frc.robot.logging;

import java.util.Hashtable;
import java.util.ArrayList;

public class csvHelper {

    public static final String logigngDirPath = "/c/Users/SciBorgs/Desktop/Test Folder/DeepSpace-2019/src/main/java/frc/robot/logging/";
    // Above is the path to the logging director
    public static final String mainLoggingFile = "data.csv";

    // name of the column = string at the top of the column

    public static void addRow(Hashtable<String,Object> row){
        // Should add a row to the next empty row of the sheet
        // The key of the hashtable should be the name of the column
        return;
    }

    public static void addColumn(String columnName){
        // Adds a rightmost column with every cell empty except the top one w/ the column name
        return;
    }

    public static ArrayList<String> getColumns(){
        // Gives a list of the column names in order
        return new ArrayList<String>();
    }

}