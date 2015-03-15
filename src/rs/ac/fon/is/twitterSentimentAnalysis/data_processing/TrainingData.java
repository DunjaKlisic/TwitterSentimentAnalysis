package rs.ac.fon.is.twitterSentimentAnalysis.data_processing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class TrainingData {
	
	private ArrayList<String> tweets;
	
	public TrainingData(ArrayList<String> tweets){
		this.tweets=tweets;
	}
	
	public void createTrainingDataset(String fileName) throws IOException{
		
			FastVector sentimentClasses = new FastVector(2);
			sentimentClasses.addElement("positive");
			sentimentClasses.addElement("negative");
			
			
			Attribute sentimentAttr = new Attribute("sentiment_class", sentimentClasses);
			Attribute textAttr = new Attribute("text", (FastVector) null);
						
			FastVector attributes = new FastVector();
			attributes.addElement(textAttr);
			attributes.addElement(sentimentAttr);
			
			Instances data = new Instances("trainingData", attributes, 1);
			data.setClassIndex(1);

			for (int i=0; i<tweets.size();i++){
				Instance instance = new Instance(2);
				instance.setDataset(data);
				instance.setValue(textAttr, tweets.get(i));
				if(i<100)
					instance.setValue(sentimentAttr, "positive");
				else
					instance.setValue(sentimentAttr, "negative");
				data.add(instance);		
			}
			 PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
			 out.write(data.toString());
			 out.flush();
			 out.close();
		
	}

}
