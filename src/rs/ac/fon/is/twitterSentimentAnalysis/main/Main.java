package rs.ac.fon.is.twitterSentimentAnalysis.main;

import java.io.FileNotFoundException;
import rs.ac.fon.is.twitterSentimentAnalysis.data.DataCollection;
import rs.ac.fon.is.twitterSentimentAnalysis.data.Listener;

import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

public class Main {
	public static void main(String[] args) throws FileNotFoundException{
		
		TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
		DataCollection dc = new DataCollection(twitterStream);
		Listener l = new Listener(dc);
		twitterStream.addListener(l);
        twitterStream.sample();
        
		
		
		
	}

}
