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
import java.util.Objects;

public class Tweet implements Comparable<Tweet> {
	//private static Map<String, List<Tweet>> df = new LinkedHashMap<>();
	private Map<String, Double> tf = new HashMap<>();
	private Set<String> words = new TreeSet<>();
	private double retweet;
	private double tweetScore;
	private String text;
	public Tweet(List<String> splitWords,String text,double retweet) {
		this.retweet = retweet;
		this.text = text;
		double wordTotal = 0;
	    for (String word : splitWords) {
	    	tf.put(word, tf.getOrDefault(word,0.0)+1.0);
	    	wordTotal++;
	    	//df.put(word, tf.getOrDefault(word,new ArrayList<>()).add(this));
	    }
	    for (String word : splitWords) {
	    	tf.put(word, tf.get(word)/wordTotal);
	    	words.add(word);
	    }
	    this.tweetScore = retweet;
	}
	public double retweets() {
		return retweet;
	}
	public void setTweetScore(double average) {
		tweetScore = retweet/average;
	}
	public double tweetScore() {
		return tweetScore;
	}
	public Set<String> words() {
		return words;
	}
	public Map<String, Double> tf() {
		return tf;
	}
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}
		// object must be Test at this point
		Tweet test = (Tweet)obj;
		return text.equals(test.text);
	}
	public int hashCode(){
		return Objects.hash(text);
	}
}