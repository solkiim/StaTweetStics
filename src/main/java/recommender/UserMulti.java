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
import edu.brown.cs.suggest.ORM.*;
import edu.brown.cs.OAuth.Oauth;
public class UserMulti implements User {
	private List<String> handles;
	private Set<Tweet> tweets = new HashSet<>();
	public UserMulti(List<String> handles) {
		this.handles = handles;
		for (String handle : handles) {
			Set<Tweet> tweets3 = TweetProxy.fromUserHandle(handle);
			Set<Tweet> tweets2 = new HashSet<>();
			if (tweets3 == null || tweets3.size() == 0) {
				try {
					System.out.println("Querying Twitter for @"+handle+"...");
					Oauth oa = new Oauth(handle,new ArrayList<>(),Db.getURL());
					oa.run();
					tweets3 = TweetProxy.fromUserHandle(handle);
				} catch (Exception e) {
					//System.out.println("ERROR: an error has occured");
					throw new RuntimeException(e);
				}
			}
			for (Tweet t : tweets3) {
				//if (!t.text().contains("@")) {
					tweets2.add(t);
				//}
			}
			
			TweetStringParser tsp = new TweetStringParser();
			for (Tweet twt : tweets2) {
				twt.parse(tsp);
				if (twt.words().size() != 0) {
					tweets.add(twt);
				}
			}
			
		}
	}
	@Override
	public int size() {
		return tweets.size();
	}
	@Override
	public String getHandle() { return (handles.toString()); }
	@Override
	public Set<Tweet> getTweets() { return tweets; }
}