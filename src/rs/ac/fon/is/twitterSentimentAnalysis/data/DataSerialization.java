package rs.ac.fon.is.twitterSentimentAnalysis.data;


import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.google.gson.Gson;

import twitter4j.Status;


public class DataSerialization {
	private PrintWriter out;
	
	public DataSerialization(){
		try {
			out = new PrintWriter("tweets_sr.json");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void serializeStatusesToJSON(ArrayList<Status> tweets){
		Gson gson = new Gson();
		for (int i=0; i<tweets.size(); i++){
			out.println(gson.toJson(tweets.get(i)));
			
			
		}
		
		
		
		
	}
	public void flush(){
		out.flush();
	}

}
