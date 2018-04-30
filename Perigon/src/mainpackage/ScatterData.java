package mainpackage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ScatterData {
	static ArrayList<String> curves = new ArrayList<String>();
	String date;
	
	public ScatterData() {

	}
	
	public void parseData(File file) {
		try
        {
            BufferedReader in = new BufferedReader(new FileReader(file));

            String line;
            boolean cursor = false;
            while ((line = in.readLine()) != null) {
            	if(line.contains("Date Exported")) {
            		date = line;
            	}
            	if(line.contains("~A")) {
            		cursor = true;
            	}else {
            		if(cursor) {
            			//System.out.println(line);
            			curves.add(line);            			
            		}
            	}
            }   
            
        } catch (IOException e)
        {
            System.out.println("File I/O error!");
        }
	}
	
	public static ArrayList<String> getCurves() {	
		return curves;
	}
	
	public String getDate() {
		return date;
	}
	
	public void resetCurves() {
		curves = new ArrayList<String>();
	}
}
