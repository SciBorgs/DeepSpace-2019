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

    private void updateLastLine() throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String tempLastLine = "";
        String current = "";
        while((current = reader.readLine()) != null){
            fileContent += current;
            tempLastLine = current;
        }
        lastLine = tempLastLine;
        reader.close();
    }

    public Hashtable<String, String> getLastRow() throws IOException{
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

    public void addRow(Hashtable<String,String> row)throws IOException{
        // Should add a row to the next empty row of the sheet
        // The key of the hashtable should be the name of the column
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
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

    public void addColumn(String columnName)throws IOException{
        // Adds a rightmost column with every cell empty except the top one w/ the column name
        File oldFile = new File(fileName);
        File newFile = new File("0"+fileName);
        BufferedReader reader = new BufferedReader(new FileReader(oldFile));
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
        
        boolean firstRow = true;
        String line = "";
        while((line = reader.readLine()) != null){
            if(firstRow){
                writer.println(line+","+columnName);
                firstRow = false;
            }else{
                writer.println(line);
            }
        }
        reader.close();
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
