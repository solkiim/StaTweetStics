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

public interface User {
	int size();
	String getHandle();
	Set<Tweet> getTweets();
}