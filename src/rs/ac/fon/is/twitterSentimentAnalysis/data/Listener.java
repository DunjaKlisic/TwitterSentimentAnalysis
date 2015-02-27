package rs.ac.fon.is.twitterSentimentAnalysis.data;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

public class Listener implements StatusListener{
	
	private DataCollection dc;
	
	public Listener (DataCollection dc){
		this.dc=dc;
	}
	
	public void onException(Exception arg0) {
		
	}

	public void onDeletionNotice(StatusDeletionNotice arg0) {
		
	}

	public void onScrubGeo(long arg0, long arg1) {
		
		
	}

	public void onStallWarning(StallWarning arg0) {
		
		
	}

	public void onStatus(Status status) {
		dc.collect(status);
		
	}

	public void onTrackLimitationNotice(int arg0) {
		
		
	}

}
