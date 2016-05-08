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
import java.util.Objects;
import com.google.common.collect.Multiset;
import com.google.common.collect.HashMultiset;
import java.util.concurrent.ConcurrentHashMap;
import edu.brown.cs.suggest.Graph.Vertex;
import edu.brown.cs.suggest.Graph.Edge;
import edu.brown.cs.suggest.ORM.Tweet;

public class Word implements Comparable<Word>, Vertex<Word, Tweet> {
	private static Map<String,Word> cache = new ConcurrentHashMap<>();
	private Set<Tweet> tweets = new HashSet<>();
	private final String word;
	private double score;
	public Word(String word){
		this.word = word;
	}
	public Word(String word,Set<Tweet> tweets){
		this.tweets.addAll(tweets);
		this.word = word;
	}
	public static Word valueOf(String word){
		Word w = cache.get(word);
		if (w == null) {
			w = new Word(word);
			cache.put(word,w);
		}
		return w;
	}
	public static Word valueOf(String word,Tweet tweet){
		Word w = cache.get(word);
		if (w == null) {
			w = new Word(word);
			cache.put(word,w);
		}
		w.tweets.add(tweet);
		return w;
	}
	public Set<Tweet> getTweets() {
		return tweets;
	}
	public void addTweet(Tweet twt) {
		tweets.add(twt);
	}
	public Set<Tweet> getEdges() {
		return getTweets();
	}
	public double nodeWeight() {
		return score;
	}
	public String getWord() {
		return word;
	}
	public String printWordData() {
		return String.format("word:{val: %s, Tweets:{ %s } val-end: %s}",word,tweets.size(),word);
	}
	public void setScore(double score){
		this.score = score;
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
		Word test = (Word)obj;
		return word.equals(test.word);
	}
	@Override
	public int hashCode(){
		return Objects.hash(word);
	}
	@Override
	public int compareTo(Word obj) {
		return word.compareTo(obj.word);
	}
	@Override
	public String toString() {
		return word;
	}
	public static void clearCache() {
		cache.clear();
	}
}