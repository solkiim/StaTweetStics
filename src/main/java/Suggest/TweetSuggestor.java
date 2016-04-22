// package edu.brown.cs.suggest;
// import java.util.Arrays;
// import com.google.common.base.Splitter;
// import com.google.common.base.CharMatcher;
// import java.util.List;
// import java.util.ArrayList;
// import java.sql.SQLException;
// import java.io.File;
// import java.util.LinkedHashMap;
// import java.io.InputStreamReader;
// import java.io.BufferedReader;
// import java.io.IOException;
// import java.util.Iterator;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.Set;

// public class TweetSuggestor implements Suggestor<String> {
// 	Parser<Tweet> parser = new TweetParser();
// 	Ranker<String> ranker = null;
// 	//Map<String,Word> wordMap = null;
// 	List<Tweet> tweets = new ArrayList<>();
// 	public void parse(String raw){
// 		tweets.add(parser.parse(raw));
// 	}
// 	public void parse(List<String> raw){
// 		tweets.addAll(parser.parse(raw));
// 	}
// 	public List<String> rank() {
// 		TweetRanker r = new TweetRanker(tweets);
// 		//wordMap = r.getWords();
// 		ranker = r;
// 		return ranker.rank();
// 	}
// 	public List<String> rank(int size){
// 		TweetRanker r = new TweetRanker(tweets);
// 		ranker = r;
// 		return ranker.rank(size);
// 	}
// }