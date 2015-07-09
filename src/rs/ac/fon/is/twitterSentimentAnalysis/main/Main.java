package rs.ac.fon.is.twitterSentimentAnalysis.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import rs.ac.fon.is.twitterSentimentAnalysis.analysis.SentimentAnalysis;
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
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.Logistic;
import weka.classifiers.trees.J48;

public class Main {
	public static void main(String[] args) throws FileNotFoundException{
		
		DataCollection dc = new DataCollection();
		DataSerialization ds = new DataSerialization();
		
		
		
		dc.collectPositiveTweets();
		System.out.println("Prikupljeni pozitivni");
		ds.serializeStatusesToJSON(dc.getTrainingArray(),"data/positive.json");
		
		
		
		
		dc.collectNegativeTweets();
		System.out.println("Prikupljeni negativni");
		ds.serializeStatusesToJSON(dc.getTrainingArray(),"data/negative.json");
		
		
		DataDeserialization dd = new DataDeserialization();
		dd.deserializeStatusesFromJSON("data/positive.json");
		dd.deserializeStatusesFromJSON("data/negative.json");
		
		
		DataFormatting df = new DataFormatting(dd.getStatuses());
		df.reduceFeatures();
		
		TrainingData td = new TrainingData(df.getTweets());
		try {
			td.createTrainingDataset("data/trainingDataSet.arff");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		SentimentAnalysis sa = new SentimentAnalysis();
		try {
			System.out.println("-------------------------------------------------");
			System.out.println("Naive Bayes, unigrami:");
			sa.buildClassifier(new NaiveBayes(), "data/trainingDataSet.arff", 1);
			sa.classifyATweet("Ovaj dan se nikada nece zavrsiti :(");
			
			
			
			
			System.out.println("-------------------------------------------------");
			System.out.println("Naive Bayes, bigrami:");
			sa.buildClassifier(new NaiveBayes(), "data/trainingDataSet.arff", 2);
			sa.classifyATweet("Ovaj dan se nikada nece zavrsiti :(");			
			
			
			System.out.println("-------------------------------------------------");
			System.out.println("Maximum entropy, unigrami:");
			sa.buildClassifier(new Logistic(), "data/trainingDataSet.arff", 1);
			sa.classifyATweet("Ovaj dan se nikada nece zavrsiti :(");
			
			
			
			System.out.println("-------------------------------------------------");
			System.out.println("Maximum entropy, bigrami:");
			sa.buildClassifier(new Logistic(), "data/trainingDataSet.arff", 2);
			sa.classifyATweet("Ovaj dan se nikada nece zavrsiti :(");
			
			
			
			System.out.println("-------------------------------------------------");
			System.out.println("Support vector machines, unigrami:");
			sa.buildClassifier(new LibSVM(), "data/trainingDataSet.arff", 1);
			sa.classifyATweet("Ovaj dan se nikada nece zavrsiti :(");
			
			
			System.out.println("-------------------------------------------------");
			System.out.println("Support vector machines, bigrami:");
			sa.buildClassifier(new LibSVM(), "data/trainingDataSet.arff", 2);
			sa.classifyATweet("Ovaj dan se nikada nece zavrsiti :(");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
	
	}

}
