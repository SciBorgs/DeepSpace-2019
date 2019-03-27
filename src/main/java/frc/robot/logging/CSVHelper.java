package frc.robot.logging;

import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.io.*;

public class CSVHelper {

    private String fileName;
    private BufferedReader reader;
    private PrintWriter writer;
    private StringTokenizer line;

    // name of the column = string at the top of the column

    public CSVHelper(String filename) throws IOException{
        fileName = filename;
        reader = new BufferedReader(new FileReader(fileName));
        writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
    }

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