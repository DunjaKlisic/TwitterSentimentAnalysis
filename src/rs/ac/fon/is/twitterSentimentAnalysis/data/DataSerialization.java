package rs.ac.fon.is.twitterSentimentAnalysis.data;



import java.io.BufferedWriter;
import java.io.FileNotFoundException;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.google.gson.Gson;


import twitter4j.Status;



public class DataSerialization {
	private PrintWriter out;
	
	
	public DataSerialization(){
		
	}
	
	
	
	


	public void serializeStatusesToJSON(ArrayList<Status> tweets,String fileName){
		try {
			out = new PrintWriter(new BufferedWriter((new OutputStreamWriter(new FileOutputStream(fileName, true), "UTF-8"))));

			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Gson gson = new Gson();
		for (int i=0; i<tweets.size(); i++){
			out.println(gson.toJson(tweets.get(i)));
			
			
		}
		out.close();
		
	}
	
	
	
	public void flush(){
		out.flush();
	}

}
