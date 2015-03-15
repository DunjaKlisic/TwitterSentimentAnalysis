package rs.ac.fon.is.twitterSentimentAnalysis.data_processing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import twitter4j.Status;

public class DataDeserialization {
	private BufferedReader in;
	private ArrayList<Status> statuses;
	
	public DataDeserialization(){
		statuses = new ArrayList<Status>();
	}
	
	
	public ArrayList<Status> getStatuses() {
		return statuses;
	}


	public void setStatuses(ArrayList<Status> statuses) {
		this.statuses = statuses;
	}


	public void deserializeStatusesFromJSON(String fileName){
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
			String s;
			while ((s=in.readLine())!=null){
				Status status = TwitterObjectFactory.createStatus(s);
				statuses.add(status);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}
	public void showCollectedStatuses(){
		for(Status s : statuses)
			System.out.println(s.getText());
	}
}
