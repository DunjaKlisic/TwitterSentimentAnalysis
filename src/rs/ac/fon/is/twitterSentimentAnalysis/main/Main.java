package rs.ac.fon.is.twitterSentimentAnalysis.main;

import java.io.FileNotFoundException;
import java.io.IOException;

import rs.ac.fon.is.twitterSentimentAnalysis.data.DataCollection;
import rs.ac.fon.is.twitterSentimentAnalysis.data.DataSerialization;
import rs.ac.fon.is.twitterSentimentAnalysis.data_processing.DataDeserialization;
import rs.ac.fon.is.twitterSentimentAnalysis.data_processing.DataFormatting;
import rs.ac.fon.is.twitterSentimentAnalysis.data_processing.TrainingData;

import twitter4j.FilterQuery;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

public class Main {
	public static void main(String[] args) throws FileNotFoundException{
		
		DataCollection dc = new DataCollection();
		dc.collectPositiveTweets();
		dc.collectNegativeTweets();
		
		DataSerialization ds = new DataSerialization();
		ds.serializeStatusesToJSON(dc.getTrainingArray(),"data/tweets1.json");
		
		DataDeserialization dd = new DataDeserialization();
		dd.deserializeStatusesFromJSON("data/tweets1.json");
		
		DataFormatting df = new DataFormatting(dd.getStatuses());
		df.reduceFeatures();
		
		TrainingData td = new TrainingData(df.getTweets());
		try {
			td.createTrainingDataset("data/trainingDataSet1.arff");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	
	}

}
