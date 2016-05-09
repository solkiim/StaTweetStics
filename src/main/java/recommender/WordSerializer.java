package edu.brown.cs.suggest;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import edu.brown.cs.suggest.ORM.Tweet;
import com.google.gson.Gson;
import java.util.Map;
import java.util.HashMap;


/**
 * A serializer for the Node interface. Turns a Node into a JSON
 * element and can convert a JSON object into a Node.
 */
public class WordSerializer implements JsonSerializer<Word> {
  private static final Gson GSON = new Gson();
  /**
   * Turns a NOde into a JSON.
   *
   * @param node The node to convert
   * @param t A type
   * @param context The JSON context
   * @return A JSON of the input node
   */
  @Override
  public JsonElement serialize(Word word, Type t,
      JsonSerializationContext context) {
    JsonObject obj = new JsonObject();
    assert word != null;
    obj.add("text", new JsonPrimitive(word.toString()));
    JsonArray jsonArray = new JsonArray();

    Map<String, Double> companiesRT = new HashMap<>();
    Map<String, Double> companiesLK = new HashMap<>();
    Map<String, Double> companies = new HashMap<>();

    int curMaxRT = -1;
    String tweetTextRT = "";
    Tweet twtRT = null;
    Tweet twtLK = null;
    int curMaxLK = -1;
    String tweetTextLK= "";
    double avgRT = 0.0;
    double avgLK = 0.0;
    double total = 0.0;
    for (Tweet tweet : word.getTweets()) {
      avgRT += (double) tweet.rawRetweets();
      avgLK += (double) tweet.rawLikes();
      total += 1.0;
      if (tweet.rawRetweets() > curMaxRT) {
        curMaxRT = tweet.rawRetweets();
        tweetTextRT = tweet.text();
        twtRT = tweet;
      }

      if (tweet.rawLikes() > curMaxLK) {
        curMaxLK = tweet.rawLikes();
        tweetTextLK = tweet.text();
        twtLK = tweet;
      }
      companiesRT.put(tweet.handle(), companiesRT.getOrDefault(tweet.handle(), 0.0) + tweet.retweets());
      companiesLK.put(tweet.handle(), companiesLK.getOrDefault(tweet.handle(), 0.0) + tweet.likes()); 
      companies.put(tweet.handle(), companies.getOrDefault(tweet.handle(), 0.0) + 1.0);   
        // JsonObject arrObj = new JsonObject();
        // arrObj.add("retweets",new JsonPrimitive(tweet.rawRetweets()));
        // arrObj.add("likes",new JsonPrimitive(tweet.rawLikes()));
        // jsonArray.add(arrObj);
    }
    avgLK /= total;
    avgRT /= total;

    for (String company : companies.keySet()) {
      companiesRT.put(company, companiesRT.get(company)/companies.get(company));
      companiesLK.put(company, companiesLK.get(company)/companies.get(company));
    }
    obj.add("avgLK",new JsonPrimitive(avgLK));
    obj.add("avgRT",new JsonPrimitive(avgRT));
    obj.add("maxRT", new JsonPrimitive(curMaxRT));
    obj.add("tweetTextRT", new JsonPrimitive(tweetTextRT));
    obj.add("maxLK", new JsonPrimitive(curMaxLK));
    obj.add("tweetTextLK", new JsonPrimitive(tweetTextLK));
    obj.add("companiesRT", GSON.toJsonTree(companiesRT));
    obj.add("companiesLK", GSON.toJsonTree(companiesLK));
    obj.add("nameRT",new JsonPrimitive(twtRT.handle()));
    obj.add("nameLK",new JsonPrimitive(twtLK.handle()));
    // Get a list of all the Twitter Handles
    //

    obj.add("data", jsonArray);
    return obj;
  }
}