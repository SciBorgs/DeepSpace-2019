package frc.robot.logging;

import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.io.*;

public class CSVHelper {

    private String fileName;
    private String fileContent;
    private ArrayList<String> topics;
    private String lastLine;

    // Precondition: File already has 1 entry that doesn't end in a comma

    public CSVHelper(String filename) throws IOException{
        fileName = filename;
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        topics = new ArrayList<String>();
        
        try{
            StringTokenizer firstRow = new StringTokenizer(reader.readLine(), ",");
        
            while(firstRow.hasMoreTokens()){
                topics.add(firstRow.nextToken());
            }
        }catch (Exception e){}
        
        reader.close();
        updateLastLine();

    }

    private void fileNotFound(){
        System.out.println("FILE NOT FOUND");
        // Probably also throw an error
    }

    private BufferedReader newBufferedReader(String fileName){
        try {
            return new BufferedReader(new FileReader(fileName));
        } catch (IOException E) {
            fileNotFound();
            return null;
        }
    }

    // Check With Bowen -> should the append be false in the cases when there is currently no bool in the constructor?
    private PrintWriter newPrintWriter(String fileName, boolean append){
        try {
            return new PrintWriter(new BufferedWriter(new FileWriter(fileName, append)));
        } catch (IOException E) {
            fileNotFound();
            return null;
        }
    }

    private BufferedReader newBufferedReader(File file){
        try {
            return new BufferedReader(new FileReader(file));
        } catch (Exception E) {
            fileNotFound();
            return null;
        }
    }

    // Check With Bowen -> should the append be false in the cases when there is currently no bool in the constructor?
    private PrintWriter newPrintWriter(File file){
        try {
            return new PrintWriter(new BufferedWriter(new FileWriter(file)));
        } catch (IOException E) {
            fileNotFound();
            return null;
        }
    }

    private String readLine(BufferedReader reader){
        try {
            return reader.readLine();
        } catch (IOException E){
            fileNotFound();
            return null;
        }
    }

    private void closeReader(BufferedReader reader){
        try {
            reader.close();
        } catch (IOException E){
            fileNotFound();
        }
    }

    private void updateLastLine(){
        BufferedReader reader = newBufferedReader(fileName);
        String tempLastLine = "";
        String current = "";
        while((current = readLine(reader)) != null){
            fileContent += current;
            tempLastLine = current;
        }
        lastLine = tempLastLine;
        closeReader(reader);
    }

    public Hashtable<String, String> getLastRow(){
        updateLastLine();
        StringTokenizer lastLineString = new StringTokenizer(lastLine, ",");
        Hashtable<String, String> lastrow = new Hashtable<String, String>();
        for(int i = 0;i < topics.size(); i++){
            String topic = topics.get(i);
            String value = "";
            try{
                value = lastLineString.nextToken();
            }catch (Exception e){}
            lastrow.put(topic, value);
        }

        return lastrow;
    }

    public void addRow(Hashtable<String,String> row){
        // Should add a row to the next empty row of the sheet
        // The key of the hashtable should be the name of the column
        PrintWriter writer = newPrintWriter(fileName, true);
        String content = "";
        for(int i = 0; i < topics.size(); i++){
            String topic = topics.get(i);
            content += row.get(topic) + ",";
            
        }
        int contentSize = content.length();
        content = content.substring(0, contentSize - 1);
        writer.println(content);
        writer.close();
    }

    public void addColumn(String columnName){
        // Adds a rightmost column with every cell empty except the top one w/ the column name
        File oldFile = new File(fileName);
        File newFile = new File("0"+fileName);
        BufferedReader reader = newBufferedReader(oldFile);
        PrintWriter writer = newPrintWriter(newFile);
        
        boolean firstRow = true;
        String line = "";
        while((line = readLine(reader)) != null){
            if(firstRow){
                writer.println(line+","+columnName);
                firstRow = false;
            }else{
                writer.println(line);
            }
        }
        closeReader(reader);
        oldFile.delete();
        writer.close();
        newFile.renameTo(oldFile);
        topics.add(columnName);
    }

    public ArrayList<String> getColumns(){
        // Gives a list of the column names in order
        return topics;
    }

}
