package rs.ac.fon.is.twitterSentimentAnalysis.data;


import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;

public class DataCollection{
	
	private ArrayList<Status> trainingArray;
	private TwitterStream twitterStream;
	
	
	
	public DataCollection(TwitterStream twitterStream){
		trainingArray = new ArrayList<Status>();
		this.twitterStream = twitterStream;
	}
	
	

	public ArrayList<Status> getTrainingArray() {
		return trainingArray;
	}



	public void setTrainingArray(ArrayList<Status> trainingArray) {
		this.trainingArray = trainingArray;
	}



	public TwitterStream getTwitterStream() {
		return twitterStream;
	}



	public void setTwitterStream(TwitterStream twitterStream) {
		this.twitterStream = twitterStream;
	}

	public void collect(Status status){
		DataSerialization ds = new DataSerialization();
		if((trainingArray.size()<10) && (status.getLang().equals("sr")) && (status.isRetweet()==false)){
				//&& ((status.getText().contains(":)") || (status.getText().contains(":(")))) && !(status.getText().contains(":P"))){
			trainingArray.add(status);
			System.out.println("Added: "+trainingArray.size());

		}
		if (trainingArray.size()== 10){
			ds.serializeStatusesToJSON(trainingArray);
			ds.flush();
			System.exit(0);
		}
			
	}
	

}
