package edu.brown.cs.suggest;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.Random;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.io.IOException;
import edu.brown.cs.suggest.ORM.*;
/**

NEED TO CHANGE

*/
public class LDA {
	public double alpha; // Hyper-parameter alpha
	public double beta; // Hyper-parameter alpha
	public int numTopics; // Number of topics
	public int numIterations; // Number of Gibbs sampling iterations
	public int topWords; // Number of most probable words for each topic

	public double alphaSum; // alpha * numTopics
	public double betaSum; // beta * vocabularySize

	public List<List<Integer>> corpus; // Word ID-based corpus
	public List<List<Integer>> topicAssignments; // Topics assignments for words
													// in the corpus
	public int numDocuments; // Number of documents in the corpus
	public int numWordsInCorpus; // Number of words in the corpus

	public HashMap<Word, Integer> word2IdVocabulary; // Vocabulary to get ID
														// given a word
	public HashMap<Integer, Word> id2WordVocabulary; // Vocabulary to get word
														// given an ID
	public int vocabularySize; // The number of word types in the corpus
	public HashMap<Integer, Tweet> id2Doc;
	// numDocuments * numTopics matrix
	// Given a document: number of its words assigned to each topic
	public int[][] docTopicCount;
	// Number of words in every document
	public int[] sumDocTopicCount;
	// numTopics * vocabularySize matrix
	// Given a topic: number of times a word type assigned to the topic
	public int[][] topicWordCount;
	// Total number of words assigned to a topic
	public int[] sumTopicWordCount;
	public Random random = new Random();

	// Double array used to sample a topic
	public double[] multiPros;
	public int savestep = 0;
	public LDA(double inAlpha, double inBeta,  int inNumTopics, 
		int inNumIterations, int inTopWords, int inSaveStep, Set<Tweet> docs)  {

		alpha = inAlpha;
		beta = inBeta;
		numTopics = inNumTopics;
		numIterations = inNumIterations;
		topWords = inTopWords;
		savestep = inSaveStep;
		id2Doc = new HashMap<>();
		word2IdVocabulary = new HashMap<>();
		id2WordVocabulary = new HashMap<>();
		corpus = new ArrayList<List<Integer>>();
		numDocuments = 0;
		numWordsInCorpus = 0;
		int indexWord = -1;
		int indexDoc = -1;
		for (Tweet twt : docs) {
			indexDoc++;
			id2Doc.put(indexDoc,twt);
			List<Integer> document = new ArrayList<>();
			for (Word word : twt.words()) {
				if (word2IdVocabulary.containsKey(word)) {
					document.add(word2IdVocabulary.get(word));
				} else {
					indexWord += 1;
					word2IdVocabulary.put(word, indexWord);
					id2WordVocabulary.put(indexWord,word);
					document.add(indexWord);
				}
			}
			numDocuments++;
			numWordsInCorpus += document.size();
			corpus.add(document);
			

		}
		vocabularySize = word2IdVocabulary.size();
		docTopicCount = new int[numDocuments][numTopics];
		topicWordCount = new int[numTopics][vocabularySize];
		sumDocTopicCount = new int[numDocuments];
		sumTopicWordCount = new int[numTopics];
		for (int tIndex = 0; tIndex < numTopics; tIndex++)
			for (int dIndex = 0; dIndex < numDocuments; dIndex++)
				docTopicCount[dIndex][tIndex] = 0;
		multiPros = new double[numTopics];
		for (int i = 0; i < numTopics; i++) {
			multiPros[i] = 1.0 / numTopics;
		}

		alphaSum = numTopics * alpha;
		betaSum = vocabularySize * beta;

		System.out.println("Corpus size: " + numDocuments + " docs, "+ numWordsInCorpus + " words");
		System.out.println("Vocabuary size: " + vocabularySize);
		System.out.println("Number of topics: " + numTopics);
		System.out.println("alpha: " + alpha);
		System.out.println("beta: " + beta);
		System.out.println("Number of sampling iterations: " + numIterations);
		System.out.println("Number of top topical words: " + topWords);
		initialize();
	}
	public void initialize() {
		System.out.println("Randomly initializing topic assignments ...");

		topicAssignments = new ArrayList<List<Integer>>();

		for (int i = 0; i < numDocuments; i++) {
			List<Integer> topics = new ArrayList<Integer>();
			int docSize = corpus.get(i).size();
			for (int j = 0; j < docSize; j++) {
				int topic = nextDiscrete(multiPros); // Sample a topic
				// Increase counts
				docTopicCount[i][topic] += 1;
				topicWordCount[topic][corpus.get(i).get(j)] += 1;
				sumDocTopicCount[i] += 1;
				sumTopicWordCount[topic] += 1;

				topics.add(topic);
			}
			topicAssignments.add(topics);
		}
	}
	public void inference() {
		System.out.println("Running Gibbs sampling inference: ");
		for (int iter = 1; iter <= numIterations; iter++) {
			//System.out.println("\tSampling iteration: " + (iter));
			// System.out.println("\t\tPerplexity: " + computePerplexity());
			sample();

			if ((savestep > 0) && (iter % savestep == 0) && (iter < numIterations)) {
				System.out.println("\t\tSaving the output from the " + iter + "^{th} sample");
				//expName = orgExpName + "-" + iter;
				printTopicAssignmentRankWords();
			}
		}
		System.out.println("Sampling completed!");

	}
	public void sample(){
		for (int dIndex = 0; dIndex < numDocuments; dIndex++) {
			int docSize = corpus.get(dIndex).size();
			for (int wIndex = 0; wIndex < docSize; wIndex++) {
				// Get current word and its topic
				int topic = topicAssignments.get(dIndex).get(wIndex);
				int word = corpus.get(dIndex).get(wIndex);

				// Decrease counts
				docTopicCount[dIndex][topic] -= 1;
				// docTopicSum[dIndex] -= 1;
				topicWordCount[topic][word] -= 1;
				sumTopicWordCount[topic] -= 1;

				// Sample a topic
				for (int tIndex = 0; tIndex < numTopics; tIndex++) {
					multiPros[tIndex] = (docTopicCount[dIndex][tIndex] + alpha)
						* ((topicWordCount[tIndex][word] + beta) / (sumTopicWordCount[tIndex] + betaSum));
					// multiPros[tIndex] = ((docTopicCount[dIndex][tIndex] +
					// alpha) /
					// (docTopicSum[dIndex] + alphaSum))
					// * ((topicWordCount[tIndex][word] + beta) /
					// (topicWordSum[tIndex] + betaSum));
				}
				topic = nextDiscrete(multiPros);

				// Increase counts
				docTopicCount[dIndex][topic] += 1;
				// docTopicSum[dIndex] += 1;
				topicWordCount[topic][word] += 1;
				sumTopicWordCount[topic] += 1;

				// Update topic assignments
				topicAssignments.get(dIndex).set(wIndex, topic);
			}
		}
	}
	private int nextDiscrete(double[] probs) {
        double sum = 0.0;
        for (int i = 0; i < probs.length; i++) {
            sum += probs[i];
        }

        double r = random.nextDouble() * sum;

        sum = 0.0;
        for (int i = 0; i < probs.length; i++) {
            sum += probs[i];
            if (sum > r)
                return i;
        }
        return probs.length - 1;
    }

   private void printTopicAssignmentRankWords() {
		for (int tIndex = 0; tIndex < numTopics; tIndex++) {
			System.out.println("Topic" + tIndex + ":");

			Map<Integer, Integer> wordCount = new TreeMap<Integer, Integer>();
			for (int wIndex = 0; wIndex < vocabularySize; wIndex++) {
				wordCount.put(wIndex, topicWordCount[tIndex][wIndex]);
			}
			wordCount = sortByValueDescending(wordCount);

			Set<Integer> mostLikelyWords = wordCount.keySet();
			int count = 0;
			for (Integer index : mostLikelyWords) {
				if (count < topWords) {
					double pro = (topicWordCount[tIndex][index] + beta) / (sumTopicWordCount[tIndex] + betaSum);
					pro = Math.round(pro * 1000000.0) / 1000000.0;
					System.out.println(" " + id2WordVocabulary.get(index) + "(" + pro + ")");
					count += 1;
				} else {
					System.out.print("\n\n");
					break;
				}
			}
		}
   }
   public void print() {
   	printTopicAssignmentRankWords();
   }
	public List<List<Tweet>> getTopicsToRank() {
		List<List<Tweet>> result = new ArrayList<>();
		for (int tIndex = 0; tIndex < numTopics; tIndex++) {
			Word.clearCache();
			List<Tweet> tweetSub = new ArrayList<>();
			for (int dIndex = 0; dIndex < numDocuments; dIndex++) {
				if (docTopicCount[dIndex][tIndex] > 0) {
					int docSize = corpus.get(dIndex).size();
					//System.out.println("-  dIndex: "+dIndex);
					Tweet twt = id2Doc.get(Integer.valueOf(dIndex));
					tweetSub.add(new TweetProxy(twt.getId()));
				}
			}
			result.add(tweetSub);
		}
		return result;
	}
   private static <K, V extends Comparable<? super V>> Map<K, V> sortByValueDescending(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>()
        {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2)
            {
                int compare = (o1.getValue()).compareTo(o2.getValue());
                return -compare;
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
    public void printFB() {
    	print();
		for (int tIndex = 0; tIndex < numTopics; tIndex++) {
			System.out.println("Topic"+tIndex+":");
			Set<String> wordInTopic = new HashSet<>();
			for (int wIndex = 0; wIndex < vocabularySize; wIndex++) {
				//Word w = id2WordVocabulary.get(word);
				if(topicWordCount[tIndex][wIndex] > 0){
					Word w = id2WordVocabulary.get(wIndex);
					wordInTopic.add(w.toString());
				}
			}
			for (int dIndex = 0; dIndex < numDocuments; dIndex++) {
				if (docTopicCount[dIndex][tIndex] > 0) {

					Tweet twt = id2Doc.get(dIndex);
					List<String> foreg = new ArrayList<>();
					//List<String> backg = new ArrayList<>();
					int docSize = corpus.get(dIndex).size();
					for (int wIndex = 0; wIndex < docSize; wIndex++) {
						int word = corpus.get(dIndex).get(wIndex);
						Word w = id2WordVocabulary.get(word);
						if (wordInTopic.contains(w.toString())) {
							// Get current word
							
							foreg.add(w.toString());
						}
					}
					System.out.print("  ");
					System.out.println("Tweet:");
					System.out.print("  ");
					System.out.println(" - text: "+twt.text());
					System.out.print("  ");
					System.out.println(" - words: "+twt.words());
					System.out.print("  ");
					System.out.println(" - topic words: "+foreg);
				}
			}
		}
	}

}



















