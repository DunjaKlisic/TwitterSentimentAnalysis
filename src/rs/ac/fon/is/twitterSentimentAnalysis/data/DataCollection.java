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

public class DataCollection implements StatusListener{
	
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



	public void onException(Exception arg0) {
		
		
	}

	public void onDeletionNotice(StatusDeletionNotice arg0) {
		
		
	}

	public void onScrubGeo(long arg0, long arg1) {
		
		
	}

	public void onStallWarning(StallWarning arg0) {
		
		
	}

	public void onStatus(Status status){
		
		if((trainingArray.size()<100) && (status.getLang().equals("sr"))){
			trainingArray.add(status);
			
		}
		if (trainingArray.size()==100){
			DataSerialization ds = new DataSerialization();
			try {
				ds.serializeStatusesToJSON(trainingArray);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
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
	/*public void serialize(){
        try{
            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("tweets.json")));
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
	
	
	*/
	
	
	
	
	

}
