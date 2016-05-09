package edu.brown.cs.StaTweetStics;

import java.util.Arrays;
import com.google.common.base.Splitter;
import com.google.common.base.CharMatcher;
import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;
import java.io.File;
//TODO REMOVE
import java.net.InetAddress;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.sql.Connection;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;
import java.io.IOException;
import java.net.URLDecoder;
import freemarker.template.Configuration;
import spark.ExceptionHandler;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Map;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import edu.brown.cs.suggest.*;
import edu.brown.cs.suggest.ORM.*;
import edu.brown.cs.suggest.Graph.*;
import edu.brown.cs.OAuth.*;


/**
 * this is the class that is responsible for all of the GUI
 * interations for the server.
 */
public abstract class GUIServer {
	private static final int topWords = 10;
	private static final int topTopics = 5;
	private static final int displayWords = 1;
	private static final Gson GSON = new GsonBuilder()
	.registerTypeAdapter(Word.class, new WordSerializer()).create();
	private static final Splitter MY_SPLITTER = 
			Splitter.on(CharMatcher.WHITESPACE).trimResults().omitEmptyStrings();
	private static int port = 4242;

	/**
	 * this sets the serverport for the spark server.
	 * @param serverPort the port for the server
	 */
	public static void run(int serverPort) {
		port = serverPort;
		runSparkServer();

	}
	/** this runs the server. */
	public static void run() {
		runSparkServer();
	}
	/**
	 * this runs the spark server.
	 */
	private static void runSparkServer() {
		// We need to serve some simple static files containing CSS and
		// JavaScript.
		// This tells Spark where to look for urls of the form "/static/*".
		StopWords.init();
		Spark.setPort(port);
		Spark.externalStaticFileLocation("src/main/resources/static");
		Spark.exception(Exception.class, new ExceptionPrinter());
		FreeMarkerEngine freeMarker = createEngine();
		Spark.get("/StaTweetStics", new HomeHandler(), freeMarker);
		Spark.get("/userTweets", new UserHandler());
		Spark.get("/compareUserTweets", new MultipleHandler());
	}
	/**
	 * this handels the inital home site.
	 */
	private static class HomeHandler implements TemplateViewRoute {
		/**
		 * spark server handler.
		 * @param req the request
		 * @param res the response
		 * @return model and view
		 */
		@Override
		public ModelAndView handle(final Request req, final Response res) {

			Map<String, Object> variables = ImmutableMap.of("title", "StaTweetStics");

			return new ModelAndView(variables, "index.ftl");
		}
	}


	/**
	 * this creates the freemarker engine when it is needed
	 * with all important arguments.
	 * @return a new freemarkerengine
	 */
	private static FreeMarkerEngine createEngine() {
		Configuration config = new Configuration();
		File templates = new File("src/main/resources/spark/template/freemarker");
		try {
			config.setDirectoryForTemplateLoading(templates);
		} catch (IOException ioe) {
			System.out.printf("ERROR: Unable use %s for template loading.\n",
					templates);
			System.exit(1);
		}
		return new FreeMarkerEngine(config);
	}
	/** this represents a server error. */
	private static final int INTERNAL_SERVER_ERROR = 500;
	/** 
	 *this is the class that returns the exception handler for the 
	 *website.
	 */
	private static class ExceptionPrinter implements ExceptionHandler {
		/**
		 * spark server handler.
		 * @param req the request
		 * @param res the response
		 */
		@Override
		public void handle(Exception e, Request req, Response res) {
			res.status(INTERNAL_SERVER_ERROR);
			StringWriter stacktrace = new StringWriter();
			try (PrintWriter pw = new PrintWriter(stacktrace)) {
				pw.println("<pre>");
				e.printStackTrace(pw);
				pw.println("</pre>");
			}
			res.body(stacktrace.toString());
		}
	}

	/**
	 * 
	 */
	private static class UserHandler implements Route {
		private List<Word> modelHelper(List<Tweet> topic, boolean likesRT) {
			List<Word> results = new ArrayList<>();
			Ranker<Word> rank = new TweetRanker(topic,likesRT);
			Word.reset(SimilarWords.combineSimilar(Word.cache()));
			List<Word> ranks = rank.rank();
			NERanker<Word, Tweet> pr = new NERanker<>();
			pr.init(ranks);
			ranks = pr.rank();
			int w = 0;
			for (Word s : ranks) {
				if (w >= topWords) {
					continue;
				}
				results.add(s);
				w++;
			}
			System.out.println("Single: "+results);
			return results;
		}
		private List<Word> rankRanked(List<Word> res, List<Word> rset, boolean likesRT) {
			List<Word> copy = new ArrayList<>();
			for (Word w : res) {
				copy.add(w);
			}
			Collections.sort(copy, (a, b) -> {
				return -Integer.compare(a.getTweets().size(),b.getTweets().size());
			});
			if (copy.size() > 0) {
				for (Word w : copy) {
					if (!rset.contains(w)) {
						copy = new ArrayList<>();
						copy.add(w);
						return copy;
					}
				}
				return copy.subList(0,1);
			} else {
				return copy;
			}
		}
		public List<List<Word>> model(List<String> usrHandle) {
			List<List<Word>> results = new ArrayList<>(2);
			results.add(new ArrayList<>());
			results.add(new ArrayList<>());
			List<User> userList = new ArrayList<>();
			userList.add(new UserMulti(usrHandle));
			
			System.out.println("ranking - part 1");

			MyLDA4 lda = new MyLDA4(topWords,userList);
			lda.inference();
			lda.printFB();
			int u = -1;
			List<List<List<Tweet>>> usrResults = lda.getTopicsToRank();
			for (List<List<Tweet>> topics : usrResults) {
				u++;
				int t = -1;
				for (List<Tweet> topic : topics) {
					t++;
					if (t >= topTopics) {
						return results;
					}
					List<Word> r0 = modelHelper(topic,false);
					List<Word> r1 = modelHelper(topic,true);
					r0 = rankRanked(r0,results.get(0),false);
					r1 = rankRanked(r1,results.get(1),true);
					results.get(0).addAll(r0);
					results.get(1).addAll(r1);
				}
			}
			return results;
		}
		/**
		 * spark server handler.
		 * @param req the request
		 * @param res the response
		 * @return the json response object
		 */
		@Override
		public Object handle(final Request req, final Response res) {
			try(Db db = new Db()) {
				Map<String, Object> variables = new HashMap<>();
				QueryParamsMap qm = req.queryMap();
				String input = qm.value("user");
				System.out.println("user handle: "+input);
				List<String> usrHandle = new ArrayList();
				usrHandle.add(input);
				List<List<Word>> results = model(usrHandle);
				variables.put("indivRetweets",results.get(0).toArray());
				variables.put("indivLikes",results.get(1).toArray());
				return GSON.toJson(variables);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

/**
	 * 
	 */
	private static class MultipleHandler implements Route {
		private List<Word> modelHelper(List<Tweet> topic, boolean likesRT) {
			List<Word> results = new ArrayList<>();
			Ranker<Word> rank = new TweetRanker(topic,likesRT);
			Word.reset(SimilarWords.combineSimilar(Word.cache()));
			List<Word> ranks = rank.rank();
			NERanker<Word, Tweet> pr = new NERanker<>();
			pr.init(ranks);
			ranks = pr.rank();
			int w = 0;
			for (Word s : ranks) {
				if (w >= topWords) {
					continue;
				}
				results.add(s);
				w++;
			}
			System.out.println("Multi: "+results);
			return results;
		}
		private List<Word> rankRanked(List<Word> res, List<Word> rset, boolean likesRT) {
			List<Word> copy = new ArrayList<>();
			for (Word w : res) {
				copy.add(w);
			}
			Collections.sort(copy, (a, b) -> {
				return -Integer.compare(a.getTweets().size(),b.getTweets().size());
			});
			if (copy.size() > 0) {
				for (Word w : copy) {
					if (!rset.contains(w)) {
						copy = new ArrayList<>();
						copy.add(w);
						return copy;
					}
				}
				return copy.subList(0,1);
			} else {
				return copy;
			}
		}
		public List<List<Word>> model(List<String> usrHandle) {
			List<List<Word>> results = new ArrayList<>(2);
			results.add(new ArrayList<>());
			results.add(new ArrayList<>());
			List<User> userList = new ArrayList<>();
			userList.add(new UserMulti(usrHandle));
			
			System.out.println("ranking - part 1");

			MyLDA4 lda = new MyLDA4(topWords,userList);
			lda.inference();
			lda.printFB();
			int u = -1;
			//int i = -1;
			List<List<List<Tweet>>> usrResults = lda.getTopicsToRank();
			for (List<List<Tweet>> topics : usrResults) {
				u++;
				int t = -1;
				for (List<Tweet> topic : topics) {
					t++;
					if (t >= topTopics) {
						return results;
					}
					List<Word> r0 = modelHelper(topic,false);
					List<Word> r1 = modelHelper(topic,true);
					r0 = rankRanked(r0,results.get(0),false);
					r1 = rankRanked(r1,results.get(1),true);
					results.get(0).addAll(r0);
					results.get(1).addAll(r1);
				}
			}
			return results;
		}
		/**
		 * spark server handler.
		 * @param req the request
		 * @param res the response
		 * @return the json response object
		 */
		@Override
		public Object handle(final Request req, final Response res) {
			try(Db db = new Db()) {
				Map<String, Object> variables = new HashMap<>();
				QueryParamsMap qm = req.queryMap();
				//System.out.println("testVar");
				String qmarr = qm.value("usernames");
				String[] output = GSON.fromJson(qmarr , String[].class);
				List<String> usrHandle = Arrays.asList(output);
				System.out.println(usrHandle);
				//usrHandle.add(input);
				List<List<Word>> results = model(usrHandle);
				variables.put("indivRetweets",results.get(0).toArray());
				variables.put("indivLikes",results.get(1).toArray());
				return GSON.toJson(variables);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
