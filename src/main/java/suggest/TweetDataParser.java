package edu.brown.cs.suggest;
import java.util.Arrays;
import com.google.common.base.Splitter;
import com.google.common.base.CharMatcher;
import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;
import java.io.File;
import java.util.LinkedHashMap;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import edu.brown.cs.OAuth.Data;

public class TweetDataParser implements Parser<List<Tweet>, Data> {
	private static final Splitter MY_SPLITTER = Splitter.on(CharMatcher.WHITESPACE).trimResults().omitEmptyStrings();
	//private static final Splitter MY_SPLITTER2 = Splitter.on("|").trimResults().omitEmptyStrings();
	public List<Tweet> parse(Data data) {
		List<String> timeLine = data.getTimeLineData();
		List<Integer> favoriteCount = data.getFavoriteCount();
		List<Tweet> res = new ArrayList<>();
		assert timeLine.size() == favoriteCount.size() : "timeLine and count not same size";
		for (int i = 0; i < timeLine.size(); i++) {
			String text = timeLine.get(i);
			int rt = favoriteCount.get(i).intValue();
			res.add(new Tweet(MY_SPLITTER.splitToList(text),text,rt));
		}
		return res;
		
	}
	// }
	// public List<List<Tweet>> parse(List<Data> rawList) {
	// 	List<Tweet> res = new ArrayList<>();
	// 	for (String d : rawList) {
	// 		res.add(parse(d));
	// 	}
	// 	return res;
	// }
}