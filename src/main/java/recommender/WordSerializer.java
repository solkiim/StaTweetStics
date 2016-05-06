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

/**
 * A serializer for the Node interface. Turns a Node into a JSON
 * element and can convert a JSON object into a Node.
 */
public class WordSerializer implements JsonSerializer<Word> {

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
    for (Tweet tweet : word.getTweets()) {
        jsonArray.add(new JsonPrimitive(tweet.rawRetweets()));
    }
    obj.add("data", jsonArray);
    return obj;
  }
}