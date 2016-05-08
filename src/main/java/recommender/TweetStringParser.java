package edu.brown.cs.suggest;
import java.util.Arrays;
import com.google.common.base.Splitter;
import com.google.common.base.CharMatcher;
import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;
import java.io.File;
import java.util.LinkedHashMap;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.HashSet;
import edu.brown.cs.OAuth.Data;
import edu.brown.cs.suggest.ORM.Tweet;
public class TweetStringParser implements Parser<List<String>, String> {
	private static final Splitter MY_SPLITTER = Splitter.on(CharMatcher.WHITESPACE).trimResults().omitEmptyStrings();


	//private static final Splitter MY_SPLITTER2 = Splitter.on("|").trimResults().omitEmptyStrings();
	public List<String> parse(String text) {
		String newText = normilizeText(text);
		List<String> words = MY_SPLITTER.splitToList(newText);
		words = normilizeWords(words);
		return words;
	}
	public String normilizeText(String text) {

		List<Tuple<String,String>> command = new ArrayList<>();
		command.add(new Tuple("http://[^\\s]+"," "));
		command.add(new Tuple("https://[^\\s]+"," "));
		command.add(new Tuple("\\.\\.+"," "));
		command.add(new Tuple("( |^\\s)['!\"$%&'()*+,-./:;<=>?@\\[\\]^_`\\{\\|\\}~']+\\w+( |$)", " "));
		command.add(new Tuple("@\\w+", " "));//removes user
		command.add(new Tuple("[\\.\\!\\?\\,\\;\\:\\\'\\\"\\-\\”\\“\\’\\—\\…\\\\d]", ""));
		//command.add(new Tuple("[( |^)['!\"$%&'()*+,-./:;<=>?@\\[\\]^_`\\{\\|\\}~']+\\w+( |$)]{2,}", " "));
		//System.out.println("input: "+text);
		String newText = text;
		int i = 0;
		for (Tuple<String,String> r : command) {
			//i++;
			//System.out.println("input"+i+": "+newText);
			newText = newText.replaceAll(r.first(),r.second());
		}
		//i++;
		//System.out.println("input"+i+": "+newText);
		return newText;
	}
	public List<String> normilizeWords(List<String> words){
		int len = words.size();
		List<String> result = new ArrayList<>();
		for (String word : words) {
			word = word.toLowerCase();
			if (!StopWords.is(word)) {
				result.add(word);
			}
			
		}
		return result;
	}
	
	// }
	// public List<List<Tweet>> parse(List<Data> rawList) {
	// 	List<Tweet> res = new ArrayList<>();
	// 	for (String d : rawList) {
	// 		res.add(parse(d));
	// 	}
	// 	return res;
	// }
}