package edu.brown.cs.wflotte.bacon;
import java.util.Arrays;
import com.google.common.base.Splitter;
import com.google.common.base.CharMatcher;
import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;
import java.io.File;
//TODO REMOVE
import java.net.InetAddress;

import java.util.LinkedHashMap;
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
import java.util.Map;
import edu.brown.cs.wflotte.autocorrect.AutoCorrect;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
/**
* this is the class that is responsible for all of the GUI
* interations for the server.
*/
public abstract class GUIServer {
  private static final Gson GSON = new Gson();
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
    Spark.setPort(port);
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());
    FreeMarkerEngine freeMarker = createEngine();
    Spark.get("/StaTweetStics", new HomeHandler(), freeMarker);
    Spark.get("/userTweets", new userHandler(), freeMarker);
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
  * this is the class that provides for the autocorrect
  * functionality.
  */
  private static class UserHandler implements Route {
    /**
    * spark server handler.
    * @param req the request
    * @param res the response
    * @return the json response object
    */
    @Override
    public Object handle(final Request req, final Response res) {
      QueryParamsMap qm = req.queryMap();
      String input = qm.value("user");

      // TODO: 1. Get a list of Tweets from OAuth
      // TODO: 2. Pass the list of Tweets to Suggest, return top five words
      // TODO: 3. For each word, pass the word into Suggest to get an arrayList of daily likes over the past three months
      // TODO: 4. Produce a HashMap of each word to its arrayList, to return.

      Map<String, Object> variables = new HashMap<>();
      return GSON.toJson(variables);
    }
  }
}
