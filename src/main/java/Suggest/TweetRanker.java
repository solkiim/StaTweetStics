package edu.brown.cs.suggest;
import java.util.Arrays;
import com.google.common.base.Splitter;
import com.google.common.base.CharMatcher;
import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import com.google.common.collect.Multiset;
import com.google.common.collect.HashMultiset;
import java.util.PriorityQueue;
import java.util.Queue;

public class TweetRanker implements Ranker<Word> {
    Map<Word, Double> idfMap = new HashMap<>(); 
    Map<Word, Double> score = new HashMap<>();
    Map<Word, List<Tweet>> tweetwords = new HashMap<>();
    Set<Word> words = new HashSet<>();
    
    double docSize;
    public TweetRanker(List<Tweet> tweets) {
        docSize = Integer.valueOf(tweets.size()).doubleValue();
        double average = 0;
        //pass 1
        for (Tweet t : tweets) {
            for (Word word : t.words()) {
                words.add(word);
                List<Tweet> tweetList = tweetwords.getOrDefault(word, new ArrayList<Tweet>());
                tweetList.add(t);
                tweetwords.put(word, tweetList);
                idfMap.put(word, idfMap.getOrDefault(word, 0.0)+1.0);
            }
            average += t.retweets();
        }
        average /= docSize;
        //pass 2
        for (Tweet t : tweets) {
            t.setTweetScore(average);
            for (Word word : t.words()) {
                double val = idfMap.get(word);
                double alg = Math.log(docSize/(val+1.0)+1.0);
                idfMap.put(word, Double.valueOf(alg));
            }
        }
        //pass 3
        for (Tweet t : tweets) {
            for (Word word : t.words()) {
                score.put(word, t.tf().get(word.toString())*t.tweetScore()*score.getOrDefault(word,1.0));
            }
        }
        //pass 4 
        // for (String word : words) {
        //      score.put(word, score.getOrDefault(word,1)/average);
        // }
        for (Word word : words) {
            word.setScore(score.get(word).doubleValue());
        }
    }
    /**
     * @param doc  list of strings
     * @param term String represents a term
     * @return term frequency of term in document
     */
    public double tf(Tweet doc, String term) {
       return doc.tf().getOrDefault(term, Double.valueOf(0.0)).doubleValue();
    }

    /**
     * @param docs list of list of strings represents the dataset
     * @param term String represents a term
     * @return the inverse term frequency of term in documents
     */
    public double idf(String term) {
        return idfMap.getOrDefault(term, Double.valueOf(Math.log(docSize/1.0+1.0))).doubleValue();
    }

    /**
     * @param doc  a text document
     * @param docs all documents
     * @param term term
     * @return the TF-IDF of term
     */
    public double tfIdf(Tweet doc, String term) {
        return tf(doc, term) * idf(term);
    }
    public List<Tuple<Word, Double>> score() {
        List<Tuple<Word, Double>> result = new ArrayList<>();
        Queue<Tuple<Word, Double>> pq = new PriorityQueue<>(10, (a,b) ->{
            return (-a.second().compareTo(b.second()));
        });
        for (Word word : words) {
            pq.add(new Tuple(word,score.get(word)*idfMap.get(word)));
        }
        for(Tuple<Word,Double> t = pq.poll();t != null;t = pq.poll()) {
            result.add(t);
        }
        return result;
    }
    public List<Word> rank() {
        List<Word> s = new ArrayList<>();
        for (Tuple<Word, Double> t : score()) {
            s.add(t.first());
        }
        return s;
    }
    public List<Word> rank(int size) {
        List<Word> s = new ArrayList<>();
        List<Word> result = rank();
        int len = Math.min(result.size(),size);
        for (int i = 0; i < len; i++ ) {
            s.add(result.get(i));
        }
        return s;
    }
    public Set<Word> getWords() {
        return words;
    }


}