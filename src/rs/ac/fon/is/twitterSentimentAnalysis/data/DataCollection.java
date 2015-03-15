package rs.ac.fon.is.twitterSentimentAnalysis.data;



import java.util.ArrayList;


import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;


import twitter4j.TwitterStream;

public class DataCollection{
	
	private ArrayList<Status> trainingArray;
	private Twitter twitter;
	
	
	
	public DataCollection(){
		trainingArray = new ArrayList<Status>();
		
	}
	
	

	public ArrayList<Status> getTrainingArray() {
		return trainingArray;
	}



	public void setTrainingArray(ArrayList<Status> trainingArray) {
		this.trainingArray = trainingArray;
	}



	

	public void collectPositiveTweets(){
	    Query query = new Query();
	    query.setLang("sr");
	    query.setQuery(":)+exclude:retweets");
	    query.setCount(100);
	    QueryResult result = null;
		try {
			twitter = TwitterFactory.getSingleton();
			result = twitter.search(query);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	    for (Status status : result.getTweets()) {
	        trainingArray.add(status);
	    }
			
	}
	
	public void collectNegativeTweets(){
		Query query = new Query();
	    query.setLang("sr");
	    query.setQuery(":(+exclude:retweets");
	    query.setCount(100);
	    QueryResult result = null;
		try {
			twitter = TwitterFactory.getSingleton();
			result = twitter.search(query);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	    for (Status status : result.getTweets()) {
	        trainingArray.add(status);
	    }
	}
	
	

}
