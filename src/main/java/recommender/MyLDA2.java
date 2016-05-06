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
import edu.brown.cs.suggest.ORM.Tweet;
import edu.brown.cs.suggest.ORM.TweetProxy;
/**

NEED TO CHANGE

*/
public class MyLDA2 {
	public double alpha; // Hyper-parameter alpha
	public double beta; // Hyper-parameter alpha
	public double betaBackground;
	public int numTopics; // Number of topics
	public int numIterations; // Number of Gibbs sampling iterations
	public int topWords; // Number of most probable words for each topic
	public double[] gamma;

	public double alphaSum; // alpha * numTopics
	public double betaSum; // beta * vocabularySize
	public double betaBackgroundSum; //background beta

	public List<List<Integer>> corpus; // Word ID-based corpus
	public List<Integer> topicAssignments; // Topics assignments for words
													// in the corpus
	public int numDocuments; // Number of documents in the corpus
	public int numWordsInCorpus; // Number of words in the corpus

	public HashMap<Word, Integer> word2IdVocabulary; // Vocabulary to get ID
														// given a word
	public HashMap<Integer, Word> id2WordVocabulary; // Vocabulary to get word
														// given an ID
	public HashMap<Integer, Tweet> id2Doc;
	public int vocabularySize; // The number of word types in the corpus
	// numDocs * vocabularySize matrix marks if a word is in the foreground or background
	public int[][] foreBack;
	// Given a document: number of its words assigned to the indexed topic
	public int[][] docTopicCount;
	// numTopics * vocabularySize matrix
	// Given a topic: number of times a word type assigned to the topic
	public int[][] topicWordCount;
	// Total number of words assigned to a topic
	public int[] sumTopicWordCount;
	//the background words
	public int[] backCount;
	//forward and bacground count
	public long[] fbCount;
	public Random random = new Random();

	// Double array used to sample a topic
	public double[] multiPros;
	public int savestep = 0;
	public MyLDA2(double inAlpha, double inBeta ,double inGamma0,double inGamma1,  int inNumTopics, 
		int inNumIterations, int inTopWords, int inSaveStep, List<Tweet> docs) throws Exception {

		alpha = inAlpha;
		beta = inBeta;
		betaBackground = inBeta;
		numTopics = inNumTopics;
		numIterations = inNumIterations;
		topWords = inTopWords;
		savestep = inSaveStep;
		gamma = new double[2];
		gamma[0] = inGamma0;
		gamma[1] = inGamma1;
		word2IdVocabulary = new HashMap<>();
		id2WordVocabulary = new HashMap<>();
		id2Doc = new HashMap<>();
		corpus = new ArrayList<List<Integer>>();
		numDocuments = 0;
		numWordsInCorpus = 0;
		int indexWord = -1;
		int indexDoc = 0;
		for (Tweet twt : docs) {
			List<Integer> document = new ArrayList<>();

			id2Doc.put(indexDoc,twt);
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
			indexDoc++;
			numDocuments++;
			numWordsInCorpus += document.size();
			corpus.add(document);
			

		}

		vocabularySize = word2IdVocabulary.size();
		docTopicCount = new int[numDocuments][numTopics];
		topicWordCount = new int[numTopics][vocabularySize];
		sumTopicWordCount = new int[numTopics];
		fbCount = new long[2];
		foreBack = new int[numDocuments][vocabularySize];
		backCount = new int[vocabularySize];
		multiPros = new double[numTopics];
		for (int i = 0; i < numTopics; i++) {
			multiPros[i] = 1.0 / numTopics;
		}
		alphaSum = numTopics * alpha;
		betaSum = vocabularySize * beta;
		betaBackgroundSum = betaBackground * vocabularySize;
		System.out.println("Corpus size: " + numDocuments + " docs, "+ numWordsInCorpus + " words");
		System.out.println("Vocabuary size: " + vocabularySize);
		System.out.println("Number of topics: " + numTopics);
		System.out.println("alpha: " + alpha);
		System.out.println("beta: " + beta);
		System.out.println("Number of sampling iterations: " + numIterations);
		System.out.println("Number of top topical words: " + topWords);
		initialize();
	}
	public void initialize() throws IOException {
		System.out.println("Randomly initializing topic assignments ...");

		topicAssignments = new ArrayList<Integer>();
		for (int tIndex = 0; tIndex < numTopics; tIndex++)
			for (int dIndex = 0; dIndex < numDocuments; dIndex++)
				docTopicCount[dIndex][tIndex] = 0;
		for (int i = 0; i < numDocuments; i++) {
			//List<Integer> topics = new ArrayList<Integer>();
			int docSize = corpus.get(i).size();
			int topic = nextDiscrete(multiPros);
			docTopicCount[i][topic] += 1;
			for (int j = 0; j < docSize; j++) {
				foreBack[i][j] = 0;
				double r = random.nextDouble();
				if (r > 0.5) {
					fbCount[1]+=1;
					topicWordCount[topic][corpus.get(i).get(j)] += 1;
					sumTopicWordCount[topic] += 1;
					foreBack[i][j] = 1;
				} else {
					fbCount[0]+=1;
					backCount[corpus.get(i).get(j)] += 1;
					foreBack[i][j] = 0;
				}
				
			}
			topicAssignments.add(topic);
		}

	}
	public void inference() throws IOException {
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
	public void sample() {
		for (int dIndex = 0; dIndex < numDocuments; dIndex++) {
			int docSize = corpus.get(dIndex).size();
			sampleDoc(dIndex);
			for (int wIndex = 0; wIndex < docSize; wIndex++) { 
				sampleWord(dIndex,wIndex);
			}
		}
	}
	public void sampleDoc(int dIndex) {
		int docSize = corpus.get(dIndex).size();
		// topicAssignments = z[u][d]
		int topic = topicAssignments.get(dIndex);
		int foreWordCount = 0;
		//C_ua
		assert docTopicCount[dIndex][topic] >= 0 : "docTopicCount not greater than zero";
		assert docSize >= 0: "docSize  not greater than zero";
		docTopicCount[dIndex][topic] -= 1;
		for (int tIndex = 0; tIndex < numTopics; tIndex++) {
			//P_topic
			multiPros[tIndex] = ((docTopicCount[dIndex][tIndex] + alpha) / (docSize - 1 + alphaSum));
		}
		for (int wIndex = 0; wIndex < docSize; wIndex++) {
				if (foreBack[dIndex][wIndex] == 1) {
					// Get current word
					int word = corpus.get(dIndex).get(wIndex);
					// Decrease counts
					//C_word: 
					topicWordCount[topic][word] -= 1;
					//countAllWords:
					sumTopicWordCount[topic] -= 1;
					for (int tIndex = 0; tIndex < numTopics; tIndex++) {
						multiPros[tIndex] *= ((topicWordCount[tIndex][word] + beta + 1) / (sumTopicWordCount[tIndex] + betaBackgroundSum + 1));
					}

			}
		}
		topic = nextDiscrete(multiPros);
		topicAssignments.set(dIndex, topic);
		docTopicCount[dIndex][topic] += 1;

		for (int wIndex = 0; wIndex < docSize; wIndex++) {
			if (foreBack[dIndex][wIndex] == 1) {
				int word = corpus.get(dIndex).get(wIndex);
				topicWordCount[topic][word] += 1;
				sumTopicWordCount[topic] += 1;
			}
		}
	}
	public void sampleWord(int dIndex, int wIndex) {
		double [] fbProbs = new double[2];
		double foreProbs;
		double backProbs;
		int fb = foreBack[dIndex][wIndex];
		int word = corpus.get(dIndex).get(wIndex);
		int topic = topicAssignments.get(dIndex);
		//C_lv
		fbCount[fb]--;
		if (fb == 1) {
			topicWordCount[topic][word] -= 1;
			sumTopicWordCount[topic] -= 1;
		} else {
			//C_b
			backCount[word] -=1;
		}
		double denom = fbCount[0] + fbCount[1] + gamma[0] + gamma[1];
		fbProbs[0] = (fbCount[0] + gamma[0])/denom;
		fbProbs[1] = (fbCount[1] + gamma[1])/denom;
		fbProbs[0] *= (backCount[word] + betaBackground)/(fbCount[0] + betaBackgroundSum);
		fbProbs[1] *= (topicWordCount[topic][word] + beta)/(sumTopicWordCount[topic] + betaBackgroundSum);
		fb = nextDiscrete(fbProbs);

		fbCount[fb]++;
		if (fb == 1) {
			topicWordCount[topic][word] += 1;
			sumTopicWordCount[topic] += 1;
		} else {
			//C_b
			backCount[word] +=1;
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
	private static <K, V extends Comparable<? super V>> Map<K, V> sortByValueDescending(Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>(){
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
	public void printFB() {
		for (int tIndex = 0; tIndex < numTopics; tIndex++) {
			System.out.println("Topic"+tIndex+":");
			for (int dIndex = 0; dIndex < numDocuments; dIndex++) {
				if (docTopicCount[dIndex][tIndex] > 0) {
					Tweet twt = id2Doc.get(dIndex);
					List<String> foreg = new ArrayList<>();
					//List<String> backg = new ArrayList<>();
					int docSize = corpus.get(dIndex).size();
					for (int wIndex = 0; wIndex < docSize; wIndex++) {
						if (foreBack[dIndex][wIndex] == 1) {
							// Get current word
							int word = corpus.get(dIndex).get(wIndex);
							Word w = id2WordVocabulary.get(word);
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
					System.out.println(" - foreW: "+foreg);
				}
			}
		}
	}

}



















