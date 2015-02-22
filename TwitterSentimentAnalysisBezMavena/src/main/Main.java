package main;

import java.io.UnsupportedEncodingException;

import data.Listener;

import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

public class Main {
	public static void main(String[] args){
		
		TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
		Listener l = new Listener(twitterStream);
		/*		twitterStream.addListener(l);
        twitterStream.sample();
*/        
		l.deserialize();
		try {
			l.writeStatuses();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
