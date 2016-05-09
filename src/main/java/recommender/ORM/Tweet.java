package edu.brown.cs.suggest.ORM;

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
import java.util.Objects;
import edu.brown.cs.suggest.Graph.Vertex;
import edu.brown.cs.suggest.Graph.Edge;
import edu.brown.cs.suggest.Word;
import edu.brown.cs.suggest.Parser;
import edu.brown.cs.suggest.TweetStringParser;

/**
 * A Node is an entity that represents an intersection of Ways
 * As well as a graph vertex.
 */
public interface Tweet extends Entity, Edge<Word,Tweet> {
	void parse(Parser<List<String>,String> parse);
	String handle();
	String text();
	void setLikes();
	void setRT();
	double retweets();
	int rawRetweets();
	void setTweetScore(double average);
	double tweetScore();
	Set<Word> words();
	void replaceWord(Word wordToReplace, Word newWord);
	Set<Word> getVertex();
	double getWeight();
	Map<String, Double> tf();
}
