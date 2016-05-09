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
/**
 * An internal representation of a Node.
 */
public class TweetBean extends EntityBean<Tweet> implements Tweet {
  private Set<Word> words = new HashSet<>();
  private Map<String, Double> tf = new HashMap<>();
  private double retweet;
  private int rawRetweet;
  private double tweetScore;
  private String text;
  private String handle;
  private int rawLikes;
  private double likes;
 /**
  * the constructor for the actor bean
  *@param id the id of the actor
  *@param handle the user handle
  *@param tweets the users tweets
  */
  public TweetBean(String id,String tweetText, String handle, int faveCount, int rtCount) {
    super(id);
    this.text = tweetText;
    this.handle = handle;
    this.rawLikes = faveCount;
    this.rawRetweet = rtCount;
  }
  public void parse(Parser<List<String>,String> parser){
    postParse(parser.parse(text),text, rawRetweet);
  }
  private void postParse(List<String> splitWords,String text,int retweet) {
    this.rawRetweet = retweet;
    this.retweet = (double) rawRetweet;
    this.text = text;
    double wordTotal = 0;
      for (String word : splitWords) {
        words.add(Word.valueOf(word,this));
        tf.put(word, tf.getOrDefault(word,0.0)+1.0);
        wordTotal++;
      }
      for (String word : splitWords) {
        tf.put(word, tf.get(word)/wordTotal);
        
      }
      //this.tweetScore = retweet;
  }
  public String text() { return text; }
  public double retweets() { return retweet; }
  public int rawRetweets() { return rawRetweet; }
  public void setTweetScore(double average) {
    tweetScore = tweetScore/average;
  }
  public double tweetScore() { return tweetScore; }
  public Set<Word> words() { return words; }
  public Set<Word> getVertex() { return words; }
  public double getWeight() { return retweet; }
  public Map<String, Double> tf() { return tf; }
  public String handle() { return handle; }
  public void setLikes() {
    retweet = (double) rawLikes;
    this.tweetScore = (double) rawLikes;
  }
  public void setRT() {
    this.tweetScore = (double) rawRetweet;
    retweet = (double) rawRetweet;
  }
  // @Override
  // public int compareTo(Tweet obj) {
  //   return text.compareTo(obj.text());
  // }
  @Override
  public void replaceWord(Word wordToReplace, Word newWord) {
    words.remove(wordToReplace);
    words.add(newWord);
    Double val = tf.get(wordToReplace.toString());
    if (val != null) {
      tf.put(newWord.toString(),val);
    }
  }
  @Override
  public String toString() {
    return String.format("tweet:{text: %s, retweets: %d}",text,rawRetweet);
  }
}

// public class Tweet implements Comparable<Tweet>, Edge<Word,Tweet> {
//   //private static Map<String, List<Tweet>> df = new LinkedHashMap<>();
//   private Set<Word> words = new HashSet<>();
//   private Map<String, Double> tf = new HashMap<>();
//   //private Set<String> words = new TreeSet<>();
//   private double retweet;
//   private int rawRetweet;
//   private double tweetScore;
//   private String text;
//   public void postParse(List<String> splitWords,String text,int retweet) {
//     this.rawRetweet = retweet;
//     this.retweet = (double) rawRetweet;
//     this.text = text;
//     double wordTotal = 0;
//       for (String word : splitWords) {
//         words.add(Word.valueOf(word,this));
//         tf.put(word, tf.getOrDefault(word,0.0)+1.0);
//         wordTotal++;
//         //df.put(word, tf.getOrDefault(word,new ArrayList<>()).add(this));
//       }
//       for (String word : splitWords) {
//         tf.put(word, tf.get(word)/wordTotal);
        
//       }
//       this.tweetScore = retweet;
//   }
//   public String text() {
//     return text;
//   }
//   public double retweets() {
//     return retweet;
//   }
//   public int rawRetweets() {
//     return rawRetweet;
//   }
//   public void setTweetScore(double average) {
//     tweetScore = retweet/average;
//   }
//   public double tweetScore() {
//     return tweetScore;
//   }
//   public Set<Word> words() {
//     return words;
//   }
//   public Set<Word> getVertex() {
//     return words;
//   }
//   public double getWeight() {
//     return retweet;
//   }
//   public Map<String, Double> tf() {
//     return tf;
//   }
//   @Override
//   public boolean equals(Object obj) {
//     if(this == obj) {
//       return true;
//     }
//     if((obj == null) || (obj.getClass() != this.getClass())) {
//       return false;
//     }
//     // object must be Test at this point
//     Tweet test = (Tweet)obj;
//     return text.equals(test.text);
//   }
//   // @Override
//   // public int hashCode(){
//   //   return Objects.hash(text);
//   // }
//   @Override
//   public int compareTo(Tweet obj) {
//     return text.compareTo(obj.text);
//   }
//   @Override
//   public String toString() {
//     return String.format("tweet:{text: %s, retweets: %f}",text,retweet);
//   }
// }
