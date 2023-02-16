package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;

public class Controller {

    @FXML
    private TextArea input;

    @FXML
    private TextArea output;
       
    @FXML
    private Label rougeScore;

    @FXML
    void clear(ActionEvent event) {
    	input.clear();
    	output.clear();
    	rougeScore.setText("%");
    	
    }

    @FXML
    void copy(ActionEvent event) {
    	output.selectAll();
    	output.copy();
    }
    
   /* // slider percentage
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		slider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				currentPercentage = (int) slider.getValue();
				percentage.setText(Integer.toString(currentPercentage) + "%");
				
			}
			
		});
		
	}*/
    @FXML
    void summarize(ActionEvent event) {
    	String referencedText = input.getText();
    	referencedText = referencedText.replaceAll("(?<=[^\\w])(\"|“|”)([^\"].*?[^\\w])\\1(?=[^\\w])", "$2"); // removes any double quotes
        
        // Tokenize the text
        Tokenizer tokenizer = new Tokenizer();
        List<Sentence> sentences = tokenizer.tokenizeParagraph(referencedText);

        // Get the word frequency for each sentence
        List<HashMap<String, Integer>> sentenceWordFrequencies = new ArrayList<>();
        for (Sentence sentence : sentences) { // loop through each sentence
          HashMap<String, Integer> wordFrequency = new HashMap<>(); // create a new Hashmap to store the word frequencies for this sentence
          for (String word : sentence.getWords()) { // loop through each word in the sentence
            String wordStripped = stripAbbreviations(word);
            if (wordFrequency.containsKey(wordStripped)) { // check if the word is already in the Hashmap
              wordFrequency.put(wordStripped, wordFrequency.get(wordStripped) + 1);
            } else {
              wordFrequency.put(wordStripped, 1);
            }
          }
          sentenceWordFrequencies.add(wordFrequency);
        }

        // Calculate similarity matrix
        double[][] similarityMatrix = new double[sentences.size()][sentences.size()];
        for (int i = 0; i < sentences.size(); i++) {
    	      for (int j = 0; j < sentences.size(); j++) {
    	          if (i == j) {
    	              similarityMatrix[i][j] = 1; // sentences are the same
    	          } else {
    	              similarityMatrix[i][j] = cosineSimilarity(sentenceWordFrequencies.get(i), sentenceWordFrequencies.get(j));
    	          }
    	      }
    	  }

        // Apply TextRank algorithm
        List<String> summary = textRank(similarityMatrix, sentences);
        

        // Create list of original indices for sentences in the summary
        List<Integer> originalIndices = new ArrayList<>();
        for (int i = 0; i < sentences.size(); i++) {
          if (summary.contains(sentences.get(i).getText())) {
            originalIndices.add(i);
          }
        }

        // Sort the summary based on original indices
        originalIndices.sort(Comparator.comparingInt(o -> o));
        List<String> sortedSummary = new ArrayList<>();
        for (int index : originalIndices) {
          sortedSummary.add(sentences.get(index).getText());
        }

        // Output to GUI
        StringBuilder summarizedText = new StringBuilder();
        for (String sentence : sortedSummary) {
        	summarizedText.append(sentence).append("\n");
        }
    	 output.setText(summarizedText.toString()); // output generated summary
    	 
    	 /*String generatedText = summarizedText.toString();
    	 
    	 // Rouge-L score
    	 RougeL rougeL = new RougeL(referencedText, generatedText);

         double score2 = rougeL.getRougeScore() * 100;
         
         rougeScore.setText(String.valueOf(score2));*/
    	 
    	// Calculate the ROUGE score
    	 
    	 String reference = referencedText.replace("\n", " ");
    	 String generated = summarizedText.toString().replace("\n", " ");
    	 
    	 RougeL rouge = new RougeL(reference, generated);
    	 double score = rouge.getRougeScore() *100;
    	 rougeScore.setText(String.format("%.2f%%", score));
    }
    
    // Strip abbreviations
    private static String stripAbbreviations(String word) { //pantanggal abb asa tokenizer class
        String[] abbreviations = {};
        for (String abbr : abbreviations) {
            if (word.endsWith(abbr)) {
                word = word.substring(0, word.length() - abbr.length());
            }
        }
        return word;
    }

    // Calculate cosine similarity between two word frequency maps
    private static double cosineSimilarity(HashMap<String, Integer> wordFrequency1, HashMap<String, Integer> wordFrequency2) {
  	    double dotProduct = 0;
  	    double magnitude1 = 0;
  	    double magnitude2 = 0;
  	    List<String> stopwords = Arrays.asList("a", "an", "the", "and", "or", "of", "in", "to", "for", "with", "on", "at", 
  	    		"by", "from", "up", "down", "in", "out", "over", "under", "again", "further", "then", "once", "here", "there", 
  	    		"when", "where", "why", "how", "all", "any", "both", "each", "few", "more", "most", "other", "some", "such", 
  	    		"no", "nor", "not", "only", "own", "same", "so", "than", "too", "very", "can", "will", "just", "don't", 
  	    		"should", "now"); // ignored stopwords 
  	    for (String word : wordFrequency1.keySet()) {
  	        if (wordFrequency2.containsKey(word) && !stopwords.contains(word)) {
  	            dotProduct += wordFrequency1.get(word) * wordFrequency2.get(word);
  	        }
  	        magnitude1 += Math.pow(wordFrequency1.get(word), 2);
  	    }
  	    for (String word : wordFrequency2.keySet()) {
  	        magnitude2 += Math.pow(wordFrequency2.get(word), 2);
  	    }
  	    magnitude1 = Math.sqrt(magnitude1);
  	    magnitude2 = Math.sqrt(magnitude2);
  	    
  	    return dotProduct / (magnitude1 * magnitude2);
  	}


  	  // Implement TextRank algorithm
    private static List<String> textRank(double[][] similarityMatrix, List<Sentence> sentences) {
  	  double[] scores = new double[sentences.size()];
  	  Arrays.fill(scores, 1.0 / sentences.size());
  	  double dampingFactor = 0.85;
  	  int iterationCount = 20;
  	  for (int i = 0; i < iterationCount; i++) {
  	    double[] newScores = new double[sentences.size()];
  	    for (int j = 0; j < sentences.size(); j++) {
  	      double sum = 0;
  	      for (int k = 0; k < sentences.size(); k++) {
  	        if (similarityMatrix[j][k] > 0) {
  	          sum += similarityMatrix[j][k] * scores[k];
  	        }
  	      }
  	      newScores[j] = (1 - dampingFactor) + dampingFactor * sum;
  	    }
  	    scores = newScores;
  	  }

  	  // Sort the sentences based on their scores
  	  List<SentenceScore> sentenceScores = new ArrayList<>();
  	  for (int i = 0; i < sentences.size(); i++) {
  	    sentenceScores.add(new SentenceScore(sentences.get(i), scores[i]));
  	  }
  	  sentenceScores.sort((a, b) -> -Double.compare(a.score, b.score));
  	  
  	  

	  // Get the 50% highest scored sentences
	  List<String> summary = new ArrayList<>();
	  int numSentences = (int) Math.ceil(0.5 * sentences.size());
	  for (int i = 0; i < Math.min(numSentences, sentenceScores.size()); i++) {
	    summary.add(sentenceScores.get(i).sentence.getText());
	  }
	  return summary;
	}
    
  	private static class SentenceScore {
  	  Sentence sentence;
  	  double score;

  	  public SentenceScore(Sentence sentence, double score) {
  	    this.sentence = sentence;
  	    this.score = score; //out mo yung matataas score
  	  }
  	}


}
