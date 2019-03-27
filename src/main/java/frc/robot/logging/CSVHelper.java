package frc.robot.logging;

import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.io.*;

public class CSVHelper {

    private String fileName;
    private String fileContent;
    private BufferedReader reader;
    private PrintWriter writer;
    private ArrayList<String> topics;
    private String lastLine;

    // name of the column = string at the top of the column

    public CSVHelper(String filename) throws IOException{
        topics = new ArrayList<String>();
        fileName = filename;
        reader = new BufferedReader(new FileReader(fileName));
        writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));

        StringTokenizer firstRow = new StringTokenizer(reader.readLine());
        while(firstRow.hasMoreTokens()){
            topics.add(firstRow.nextToken());
        }

        String tempLastLine = "";
        String current = "";
        while((current = reader.readLine()) != null){
            fileContent += current;
            tempLastLine = current;
        }
        lastLine = tempLastLine;

    }

    private void updateLastLine() throws IOException{
        String tempLastLine = "";
        String current = "";
        while((current = reader.readLine()) != null){
            fileContent += current;
            tempLastLine = current;
        }
        lastLine = tempLastLine;
    }

    public Hashtable<String, String> getLastRow() {
        try{
            updateLastLine();
        }catch (IOException e){
            System.out.println("FILE NOT FOUND");
        }
        StringTokenizer lastLineString = new StringTokenizer(lastLine);
        Hashtable<String, String> lastrow = new Hashtable<String, String>();
        for(int i = 0;i < topics.size(); i++){
            lastrow.put(topics.get(i), lastLineString.nextToken());
        }

        return lastrow;
    }

    public void addRow(Hashtable<String,Object> row){
        // Should add a row to the next empty row of the sheet
        // The key of the hashtable should be the name of the column
        
        return;
    }

    public void addColumn(String columnName){
        // Adds a rightmost column with every cell empty except the top one w/ the column name
        return;
    }

    public static ArrayList<String> getColumns(){
        // Gives a list of the column names in order
        return new ArrayList<String>();
    }

}