package rs.ac.fon.is.twitterSentimentAnalysis.data;


import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.google.gson.Gson;

import twitter4j.Status;


public class DataSerialization {
	
	public void serializeStatusesToJSON(ArrayList<Status> tweets) throws FileNotFoundException{
		PrintWriter out = new PrintWriter("tweets.json");
		Gson gson = new Gson();
		for (int i=0; i<tweets.size(); i++){
			out.println(gson.toJson(tweets.get(i)));
		}
		out.close();
		
	}

}
