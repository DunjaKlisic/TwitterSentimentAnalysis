package data;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;

public class Listener implements StatusListener{
	ArrayList<Status> trainingArray;
	TwitterStream twitterStream;
	
	public Listener(TwitterStream twitterStream){
		trainingArray = new ArrayList<Status>();
		this.twitterStream = twitterStream;
	}

	public void onException(Exception arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onDeletionNotice(StatusDeletionNotice arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onScrubGeo(long arg0, long arg1) {
		// TODO Auto-generated method stub
		
	}

	public void onStallWarning(StallWarning arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onStatus(Status status){
		//System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText()+"-Language:"+status.getLang());
		if((trainingArray.size()<100) && (status.getLang().equals("sr"))){
			trainingArray.add(status);
			//System.out.println("dodat");
		}
		if (trainingArray.size()==100){
			serialize();
			twitterStream.shutdown();
		}
			
	}

	public void onTrackLimitationNotice(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void writeStatuses() throws UnsupportedEncodingException{
		PrintStream out = new PrintStream(System.out, true, "UTF-8");
		for(int i=0; i<trainingArray.size();i++){
			out.println("@" + trainingArray.get(i).getUser().getScreenName() + " - " + trainingArray.get(i).getText());
		}
	}
	public void serialize(){
        try{
            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("tweets.out")));
            for(int i=0;i<trainingArray.size();i++)
                out.writeObject(trainingArray.get(i));
                out.close();
        }catch(Exception e){
            System.out.println("Greska: "+e.getMessage());
        }
    }
	
	public void deserialize(){
        try{
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("tweets.out"));
            trainingArray.clear();
            try{
                while (true){
                    Status s = (Status)(in.readObject());
                    trainingArray.add(s);
                }
            }catch(Exception e){}
                in.close();
            }catch(Exception e){
                System.out.println("Greska: "+e.getMessage());
        }
    }
	
	
	
	
	
	
	
	

}
