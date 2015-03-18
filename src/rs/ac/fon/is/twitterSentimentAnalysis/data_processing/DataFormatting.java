package rs.ac.fon.is.twitterSentimentAnalysis.data_processing;

import java.util.ArrayList;

import twitter4j.Status;

public class DataFormatting {
	private ArrayList<Status> statuses;
	private ArrayList<String> tweets;
	
	public DataFormatting(ArrayList<Status> statuses){
		this.statuses=statuses;
		tweets = new ArrayList<String>();
	}
	
	
	
	public ArrayList<String> getTweets() {
		return tweets;
	}



	public void setTweets(ArrayList<String> tweets) {
		this.tweets = tweets;
	}



	private void stripEmoticons(){
		for(Status s : statuses){
			String noEmoticons = s.getText().replaceAll("[:;=B](')*(-)*[)(P3DSO*sp]+",""); 
			tweets.add(noEmoticons);
		}
	}
	
	private void stripUsernames(){
		for(int i=0; i<tweets.size();i++){
			String noUsernames=tweets.get(i).replaceAll("@\\w*", "USERNAME ");
			tweets.remove(i);
			tweets.add(i, noUsernames);
		}
	}
	
	private void stripURLs(){
		for(int i=0; i<tweets.size();i++){
			String noURLs=tweets.get(i).replaceAll("http(s)*://t.co/\\w*", "URL");
			tweets.remove(i);
			tweets.add(i, noURLs);
		}
	}
	
	private void stripRepeatedLetters(){
		for(int j=0; j<tweets.size();j++){
			String noRepeatedLetters = tweets.get(j).replaceAll("([a-zA-Zа-шА-Ш])\\1+","$1$1");
			tweets.remove(j);
			tweets.add(j, noRepeatedLetters);
			
		}
	}
	
	public void reduceFeatures(){
		stripEmoticons();
		stripUsernames();
		stripURLs();
		stripRepeatedLetters();
		//showCollectedTexts();
	}
	public void showCollectedStatuses(){
		for(Status s : statuses)
			System.out.println(s.getText());
	}
	
	public void showCollectedTexts (){
		for (String s: tweets)
			System.out.println(s);
	}
	

}
