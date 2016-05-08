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

public class User {
	private String handle;
	private Set<Tweet> tweets;
	public User(String handle) {
		this.handle = handle;
		tweets = TweetProxy.fromUserHandle(handle);
		Set<Tweet> tweets2 = new HashSet<>();
		if (tweets == null || tweets.size() == 0) {
			try {
				//Set<Tweet> tweets2 = new HashSet<>();
				System.out.println("Querying Twitter for @"+handle+"...");
//				Oauth oa = new Oauth(handle,new ArrayList<>(),Db.getURL());
//				oa.run();
				Oauth.setUser(handle);
				Oauth.setCompetitors(new ArrayList<>());
				Oauth.run();
				tweets = TweetProxy.fromUserHandle(handle);
			} catch (Exception e) {
				//System.out.println("ERROR: an error has occured");
				throw new RuntimeException(e);
			}
		}
		for (Tweet t : tweets) {
			if (!t.text().contains("@")) {
				tweets2.add(t);
			}
		}
		tweets = tweets2;
		TweetStringParser tsp = new TweetStringParser();
		for (Tweet twt : tweets) {
			twt.parse(tsp);
		}
	}
	public int size() {
		return tweets.size();
	}
	public String getHandle() { return handle; }
	public Set<Tweet> getTweets() { return tweets; }
}