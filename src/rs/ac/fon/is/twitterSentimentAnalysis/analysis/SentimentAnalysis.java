package rs.ac.fon.is.twitterSentimentAnalysis.analysis;

import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class SentimentAnalysis {

	private FilteredClassifier filteredClassifier;
	

	public void buildClassifier(Classifier classifier, String arffFileName, int nGramMaxSize) throws Exception {
		
			
			DataSource loader = new DataSource(arffFileName); 
			Instances trainingData = loader.getDataSet();
			
			
			trainingData.setClassIndex(1);

			NGramTokenizer tokenizer = new NGramTokenizer(); 
			tokenizer.setNGramMinSize(1); 
			tokenizer.setNGramMaxSize(nGramMaxSize); 
			
			tokenizer.setDelimiters(" \\W");
			
			StringToWordVector textToWordfilter = new StringToWordVector();
			
			
			textToWordfilter.setTokenizer(tokenizer);
			
			
			textToWordfilter.setInputFormat(trainingData);
			
			
			textToWordfilter.setWordsToKeep(10000);
			textToWordfilter.setDoNotOperateOnPerClassBasis(true);
			textToWordfilter.setLowerCaseTokens(false);
			
			Ranker ranker = new Ranker();
			ranker.setThreshold(0.0);

			AttributeSelection asFilter = new AttributeSelection();
			asFilter.setEvaluator(new InfoGainAttributeEval());
			asFilter.setSearch(ranker);

			Filter[] filters = new Filter[2];
			filters[0] = textToWordfilter;
			filters[1] = asFilter;
			
			MultiFilter multiFilter = new MultiFilter();
			multiFilter.setFilters(filters);
			

			filteredClassifier = new FilteredClassifier();
			filteredClassifier.setClassifier(classifier);
			filteredClassifier.setFilter(multiFilter);
			filteredClassifier.buildClassifier(trainingData);
			
			Evaluation eval = new Evaluation(trainingData); 
			eval.evaluateModel(filteredClassifier, trainingData); 
			
			System.out.println(eval.toSummaryString()); 
			System.out.println(eval.toMatrixString());
			
}
	
public void classifyATweet(String text) throws Exception {
		
		
		FastVector sentimentClasses = new FastVector(2);
		sentimentClasses.addElement("positive");
		sentimentClasses.addElement("negative");
		
		
		Attribute sentimentAttr = new Attribute("sentiment_class", sentimentClasses);
		Attribute textAttr = new Attribute("text", (FastVector) null);
		
		FastVector attributes = new FastVector();
		attributes.addElement(textAttr);
		attributes.addElement(sentimentAttr);
		

		Instances data = new Instances("testDataSet", attributes, 1);
		
		data.setClassIndex(1);

		
		Instance instance = new Instance(2);
		instance.setDataset(data);
		instance.setValue(textAttr, text);
		
		data.add(instance);		

		double pred = filteredClassifier.classifyInstance(instance);
		System.out.println("Sentiment of the tweet: " + data.classAttribute().value((int) pred));
	}
}
