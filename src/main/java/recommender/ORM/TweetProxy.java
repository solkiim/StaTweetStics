package edu.brown.cs.suggest.ORM;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
 * A WayProxy is the proxy version of a Way. It acts like a Way
 * Bean, but only queries the database when necessary.
 */
public class TweetProxy extends EntityProxy<Tweet> implements Tweet {
  /**
   * Simple constructor.
   *
   * @param id A way ID
   */
  public TweetProxy(String id) {
    super(id);
  }
  @Override
  public void parse(Parser<List<String>,String> parser){
    fill();
    internal.parse(parser);
  }
  @Override
  public int rawLikes() { 
    fill();
    return internal.rawLikes(); 
  }
  @Override
  public String handle() {
    fill();
    return internal.handle();
  }
  @Override
  public String text() {
    fill();
    return internal.text();
  }
  @Override
  public double retweets() {
    fill();
    return internal.retweets();
  }
  @Override
  public int rawRetweets(){
    fill();
    return internal.rawRetweets();
  }
  @Override
  public void setTweetScore(double average) {
    fill();
    internal.setTweetScore(average);
  }
  @Override
  public double tweetScore() {
    fill();
    return internal.tweetScore();
  }
  @Override
  public Set<Word> words(){
    fill();
    return internal.words();
  }
  @Override
  public Set<Word> getVertex(){
    fill();
    return internal.getVertex();
  }
  @Override
  public double getWeight(){
    fill();
    return internal.getWeight();
  }
  @Override
  public Map<String, Double> tf(){
    fill();
    return internal.tf();
  }
  @Override
  public void replaceWord(Word wordToReplace, Word newWord){
    fill();
    replaceWord(wordToReplace,newWord);
  }

  /**
   * Fills internal from Db cache.
   */
  @Override
  protected void fillFromCache() {
    if (internal == null) {
      internal = (Tweet) checkCache(id);
    }
  }
  public void setLikes() {
    fill();
    internal.setLikes();
  }
  public void setRT() {
    fill();
    internal.setRT();
  }

  /**
   * this correctly fill in the data for a Way.
   *
   * @param conn A connection to the sql database
   */
  @Override
  protected void fill(Connection conn) throws SQLException {
    String tweet = null;
    int fave_count = 0;
    int rt_count = 0;
    String id = null;
    String handle = null;
    try {
      PreparedStatement prep =
      Db.prepare("SELECT tweet,fave_count,rt_count,handle "
                            + "FROM data WHERE id = ?;");
      prep.setString(1, getId());
      try(ResultSet rs = prep.executeQuery()) {
        if (rs.next()) {
          tweet = rs.getString(1);
          fave_count = rs.getInt(2);
          rt_count = rs.getInt(3);
          handle = rs.getString(4);
        } else {
          return;
        }
      }
      

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    //System.out.println(new TweetBean(getId(), tweet, handle, fave_count, rt_count));
    internal = new TweetBean(getId(), tweet, handle, fave_count, rt_count);
  }

  /**
   * Builds a way proxy from an ID.
   *
   * @param db The database
   * @param id The ID of a way
   * @return A node proxy with ID "ID" if it exists, null otherwise
   */
  public static TweetProxy ofId(String id) {
    if (id == null) {
      return null;
    }
    return withConnection((conn) -> {
      try {
        PreparedStatement prep =
           Db.prepare("SELECT id FROM data WHERE id = ?;");
        prep.setString(1, id);
        try (ResultSet rs = prep.executeQuery()) {
          if (!rs.next()) {
            return null;
          }
          return new TweetProxy(rs.getString(1));
        }
      } catch (SQLException se) {
        throw new RuntimeException(se);
      }
    });
  }

  /**
   * Returns a list of all the Way Names in this database.
   *
   * @param db The database
   * @return A list of all the Way Names in this database
   */
  public static Set<Tweet> fromUserHandle(String handle) {
    return withConnection((conn) -> {
      try {
        PreparedStatement prep =
           Db.prepare("SELECT id FROM data WHERE handle = ?;");
        prep.setString(1, handle);
        try (ResultSet rs = prep.executeQuery()) {
          Set<Tweet> toReturn = new HashSet<>();
          while (rs.next()) {
            toReturn.add(new TweetProxy(rs.getString(1)));
          }
          return toReturn;
        }
      } catch (SQLException se) {
        throw new RuntimeException(se);
      }
    });
  }
}
