// package edu.brown.cs.StaTweetStics;

// import java.util.Arrays;
// import com.google.common.base.Splitter;
// import com.google.common.base.CharMatcher;
// import java.util.List;
// import java.util.ArrayList;
// import java.sql.SQLException;
// import java.io.File;
// //TODO REMOVE
// import java.net.InetAddress;

// import java.util.LinkedHashMap;
// import java.util.HashMap;
// import java.sql.Connection;
// import spark.ModelAndView;
// import spark.QueryParamsMap;
// import spark.Request;
// import spark.Response;
// import spark.Route;
// import spark.Spark;
// import spark.TemplateViewRoute;
// import spark.template.freemarker.FreeMarkerEngine;
// import java.io.IOException;
// import java.net.URLDecoder;
// import freemarker.template.Configuration;
// import spark.ExceptionHandler;

// import com.google.common.collect.ImmutableMap;
// import com.google.gson.Gson;
// import com.google.gson.GsonBuilder;
// import java.util.Map;
// import java.io.PrintWriter;
// import java.io.StringWriter;
// import java.net.UnknownHostException;

// import java.sql.PreparedStatement;
// import java.sql.ResultSet;

// //import edu.brown.cs.suggest.WordSerializer;
// import edu.brown.cs.suggest.*;
// import edu.brown.cs.suggest.ORM.*;
// import edu.brown.cs.suggest.Graph.*;
// import edu.brown.cs.OAuth.*;


// /**
// * this is the class that is responsible for all of the GUI
// * interations for the server.
// */
// public abstract class GUIServer {
//   private static final int TOP_WORDS = 5;
//   private static final int topTopics = 6;
//   private static final Gson GSON = new GsonBuilder()
//     .registerTypeAdapter(Word.class, new WordSerializer()).create();
//   private static final Splitter MY_SPLITTER = 
//   Splitter.on(CharMatcher.WHITESPACE).trimResults().omitEmptyStrings();
//   private static int port = 4242;
//   /**
//   * this sets the serverport for the spark server.
//   * @param serverPort the port for the server
//   */
//   public static void run(int serverPort) {
//     port = serverPort;
//     runSparkServer();
//   }
//   /** this runs the server. */
//   public static void run() {
//     runSparkServer();
//   }
//   /**
//   * this runs the spark server.
//   */
//   private static void runSparkServer() {
//     // We need to serve some simple static files containing CSS and
//     // JavaScript.
//     // This tells Spark where to look for urls of the form "/static/*".
//     Spark.setPort(port);
//     Spark.externalStaticFileLocation("src/main/resources/static");
//     Spark.exception(Exception.class, new ExceptionPrinter());
//     FreeMarkerEngine freeMarker = createEngine();
//     Spark.get("/StaTweetStics", new HomeHandler(), freeMarker);
//     Spark.get("/userTweets", new UserHandler());
//   }
//   /**
//   * this handels the inital home site.
//   */
//   private static class HomeHandler implements TemplateViewRoute {
//     /**
//     * spark server handler.
//     * @param req the request
//     * @param res the response
//     * @return model and view
//     */
//     @Override
//     public ModelAndView handle(final Request req, final Response res) {
      
//       Map<String, Object> variables = ImmutableMap.of("title", "StaTweetStics");

//       return new ModelAndView(variables, "index.ftl");
//     }
//   }


//   /**
//   * this creates the freemarker engine when it is needed
//   * with all important arguments.
//   * @return a new freemarkerengine
//   */
//   private static FreeMarkerEngine createEngine() {
//     Configuration config = new Configuration();
//     File templates = new File("src/main/resources/spark/template/freemarker");
//     try {
//       config.setDirectoryForTemplateLoading(templates);
//     } catch (IOException ioe) {
//       System.out.printf("ERROR: Unable use %s for template loading.\n",
//                         templates);
//       System.exit(1);
//     }
//     return new FreeMarkerEngine(config);
//   }
//   /** this represents a server error. */
//   private static final int INTERNAL_SERVER_ERROR = 500;
//   /** 
//   *this is the class that returns the exception handler for the 
//   *website.
//   */
//   private static class ExceptionPrinter implements ExceptionHandler {
//     /**
//     * spark server handler.
//     * @param req the request
//     * @param res the response
//     */
//     @Override
//     public void handle(Exception e, Request req, Response res) {
//       res.status(INTERNAL_SERVER_ERROR);
//       StringWriter stacktrace = new StringWriter();
//       try (PrintWriter pw = new PrintWriter(stacktrace)) {
//         pw.println("<pre>");
//         e.printStackTrace(pw);
//         pw.println("</pre>");
//       }
//       res.body(stacktrace.toString());
//     }
//   }

//   /**
//   * 
//   */
//   private static class UserHandler implements Route {
//     /**
//     * spark server handler.
//     * @param req the request
//     * @param res the response
//     * @return the json response object
//     */
//     @Override
//     public Object handle(final Request req, final Response res) {
//       try {
// 	      QueryParamsMap qm = req.queryMap();
// 	      String input = qm.value("user");

// 	      User usr = new User(input);
// 				List<User> userList = new ArrayList();
// 				userList.add(usr);
// 				System.out.println("ranking - part 1");
// 				Word.reset(SimilarWords.combineSimilar(Word.cache()));
// 				MyLDA4 lda = new MyLDA4(TOP_WORDS,userList);
// 				//System.out.println("ranking - part 2");
// 				lda.inference();
// 				System.out.println("Results");
// 				int i = 0;
// 				int u = -1;
// 				int d = -1;
// 				int w = -1;
// 				Word[][][] rankResult = new Word[usrResults.size()][][];
// 				List<List<List<Tweet>>> usrResults = lda.getTopicsToRank();
// 				for (List<List<Tweet>> topics : usrResults) {
// 					for (List<Tweet> topic : topics) {
// 						if (i > topTopics) {
// 							continue;
// 						}
// 						System.out.println("Topic "+i+": ");
// 						i++;
// 						Ranker<Word> rank = new TweetRanker(topic);
						
// 						List<Word> ranks = rank.rank();
// 						NERanker<Word, Tweet> pr = new NERanker<>();
// 						pr.init(ranks);
// 						ranks = pr.rank();
// 						int w = 0;
// 						for (Word s : ranks) {
// 							if (w >= TOP_WORDS) {
// 								continue;
// 							}
// 							w++;
// 							System.out.print("  ");
// 							System.out.println(s.printWordData());
// 						}
// 					}
// 				}
// 				//System.out.println("printFB");
// 				//lda.printFB(topTopics);
//         variables.put("yourTrending",ranks.toArray());
//         return GSON.toJson(variables);
//       } catch (Exception e) {
//         throw new RuntimeException(e);
//       }
//     }
//   }
// }
