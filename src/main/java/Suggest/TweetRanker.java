package edu.brown.cs.wflotte.termproject;
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
import java.util.TreeSet;
import com.google.common.collect.Multiset;
import com.google.common.collect.HashMultiset;
import java.util.PriorityQueue;
import java.util.Queue;

public class TweetRanker implements Ranker<String> {
    Map<String, Double> idfMap = new HashMap<>(); 
    Map<String, Double> score = new HashMap<>();
    Map<String, List<Tweet>> tweetwords = new HashMap<>();
    Set<String> words = new TreeSet<>();
    double docSize;
    public TweetRanker(List<Tweet> tweets) {
        docSize = Integer.valueOf(tweets.size()).doubleValue();
        double average = 0;
        //pass 1
        for (Tweet t : tweets) {
            for (String word : t.words()) {
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
            for (String word : t.words()) {
                double val = idfMap.get(word);
                double alg = Math.log(docSize/(val+1.0)+1.0);
                idfMap.put(word, Double.valueOf(alg));
            }
        }
        //pass 3
        for (Tweet t : tweets) {
            for (String word : t.words()) {
                score.put(word, t.tf().get(word)*t.tweetScore()*score.getOrDefault(word,1.0));
            }
        }
        //pass 4 
        // for (String word : words) {
        //      score.put(word, score.getOrDefault(word,1)/average);
        // }
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
    public List<Tuple<String, Double>> score() {
        List<Tuple<String, Double>> result = new ArrayList<>();
        Queue<Tuple<String, Double>> pq = new PriorityQueue<>(10, (a,b) ->{
            return (-a.second().compareTo(b.second()));
        });
        for (String word : words) {
            pq.add(new Tuple(word,score.get(word)*idfMap.get(word)));
        }
        for(Tuple<String,Double> t = pq.poll();t != null;t = pq.poll()) {
            result.add(t);
        }
        return result;
    }
    public List<String> rank() {
        List<String> s = new ArrayList<>();
        for (Tuple<String, Double> t : score()) {
            s.add(t.first());
        }
        return s;
    }
    public List<String> rank(int size) {
        List<String> s = new ArrayList<>();
        List<String> result = rank();
        int len = Math.min(result.size(),size);
        for (int i = 0; i < len; i++ ) {
            s.add(result.get(i));
        }
        return s;
    }
    public Map<String, Word> getWords() {
        Map<String, Word> result = new HashMap<>();
        for (String word : words) {
             result.put(word,new Word(word, tweetwords.get(word)));
        }
        return result;
    }


}