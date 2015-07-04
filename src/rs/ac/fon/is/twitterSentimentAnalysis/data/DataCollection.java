package rs.ac.fon.is.twitterSentimentAnalysis.data;



import java.util.ArrayList;
import java.util.Map;


import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterResponse;


import twitter4j.TwitterStream;
import twitter4j.api.HelpResources;
import twitter4j.conf.ConfigurationBuilder;

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
	    long lastID=Long.MAX_VALUE;
	    while (trainingArray.size()<1000){
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
		        if(status.getId() < lastID) lastID = status.getId();
		    }
		    query.setMaxId(lastID-1);
	    }
	    
	}
	
	public void collectNegativeTweets(){
		Query query = new Query();
	    query.setLang("sr");
	    query.setQuery(":(+exclude:retweets");
	    long lastID=Long.MAX_VALUE;
	    while (trainingArray.size()<1000){
		    	query.setCount(100);
			    QueryResult result = null;
				try {
					twitter = TwitterFactory.getSingleton();
					result = twitter.search(query);
				} catch (TwitterException e) {
					e.printStackTrace();
					break;
				}
			    for (Status status : result.getTweets()) {
			        trainingArray.add(status);
			        if(status.getId() < lastID) lastID = status.getId();
			    }
			    query.setMaxId(lastID-1);
			    
				}
	    }
	}
	
	


