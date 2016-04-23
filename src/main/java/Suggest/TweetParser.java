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
// import java.util.Set;


// public class TweetParser implements Parser<Tweet, String> {
// 	private static final Splitter MY_SPLITTER = Splitter.on(CharMatcher.WHITESPACE).trimResults().omitEmptyStrings();
// 	private static final Splitter MY_SPLITTER2 = Splitter.on("|").trimResults().omitEmptyStrings();
// 	public Tweet parse(String raw) {
// 		List<String> a = MY_SPLITTER2.splitToList(raw);
// 		//System.out.println(a);
// 		String text = a.get(0);
// 		double retweet = Integer.valueOf(a.get(1));
// 		//System.out.println("t:"+text+" r: "+retweet);
// 		return new Tweet(MY_SPLITTER.splitToList(text),text,retweet);

// 	}
// 	public List<Tweet> parse(List<String> rawList) {
// 		List<Tweet> res = new ArrayList<>();
// 		for (String d : rawList) {
// 			res.add(parse(d));
// 		}
// 		return res;
// 	}
// }