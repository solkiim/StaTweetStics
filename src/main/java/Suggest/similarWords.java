package edu.brown.cs.suggest;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import edu.brown.cs.suggest.*;
import edu.brown.cs.suggest.ORM.*;

public class similarWords {
	

	public similarWords() {
	}

	/**
	* Takes a list (HashMap) of words
	* Combines words which are similar
	* Similarity defined by one Levenshtein distance,
	* and the following swaps:
	*
	* K, C
	* S, Z
	* U, oo, ew
	* o, 0
	* e, 3
	* x, cks
	*/
	public static Map<String, Word> combineSimilar (Map<String, Word> wordMap) {

		// A hashmap linking each word to its 'default' (see below)
		Map<String, String> oldToNew = new HashMap<String, String>();

		// A set of all the words
		Set<String> wordSet = wordMap.keySet(); 

		// We create a 'default' for each word
		for (String word : wordSet) {
			
			String newWord = word.toLowerCase().replaceAll("c", "k").replaceAll("s", "z")
			.replaceAll("u", "ew").replaceAll("oo", "ew").replaceAll("o", "0")
			.replaceAll("e", "3").replaceAll("x", "cks");

			oldToNew.put(word, newWord);
		}

		// The map we want to return, with the combined values
		Map<String, Word> combinedMap = new HashMap<String, Word>();

		// We check if the defaults are the same, or within
		// one Levenshtein distance from each other
		for (Iterator<String> i = wordSet.iterator(); i.hasNext();) {
    		String word = i.next();

    		// Levenshtein suggestions for word
			Set<String> suggestions = levSuggestions(oldToNew.get(word), 1);

			Set<Tweet> totalSet = new HashSet<>();

			// Finds all matches or words within one Levenshtein distance
			// from the original word, and adds to tempList
			for (String otherWord: wordSet) {
				if ((oldToNew.get(word).equals(oldToNew.get(otherWord))) 
					|| (suggestions.contains(oldToNew.get(otherWord)))) {
					i.remove(otherWord);
					totalSet.addAll(wordMap.get(otherWord).tweets);
				}
			}

			combinedMap.put(word, totalSet);
   		 }
   		 return combinedMap;
	}

 /**
   * Given a word, returns all versions of the word with one substitution.
   *
   * @param word
   *          , any String
   * @return an ArrayList of all versions of 'word' with one substitution
   */
  private static ArrayList<String> substitutionHelper(String word) {
    ArrayList<String> wordList = new ArrayList<String>();
    // an empty list to which we will add words later
    String alphabet = "abcdefghijklmnopqrstuvwxyz";
    for (int i = 0; i < word.length(); i++) {
      for (int j = 0; j < alphabet.length(); j++) {
        String subbedWord = word.substring(0, i) + alphabet.charAt(j)
            + word.substring(i + 1);

        // replaces each letter in the word with each letter of the alphabet
        wordList.add(subbedWord);
      }
    }
    return wordList;
  }

  /**
   * Given a word, returns all versions of the word with one inserted letter.
   *
   * @param word
   *          , any String
   * @return an ArrayList of all versions of 'word' with one inserted letter
   *
   */
  private static ArrayList<String> insertionHelper(String word) {
    ArrayList<String> wordList = new ArrayList<String>();
    // an empty list to which we will add words later
    String alphabet = "abcdefghijklmnopqrstuvwxyz";
    for (int i = 0; i < word.length() + 1; i++) {
      for (int j = 0; j < alphabet.length(); j++) {
        String insWord = word.substring(0, i) + alphabet.charAt(j)
            + word.substring(i);
        // at each spot in the word inserts each letter of the alphabet
        wordList.add(insWord);
      }
    }
    return wordList;
  }

  /**
   * Given a word, returns all versions of the word with one deleted letter.
   *
   * @param word
   *          , any String
   * @return an ArrayList of all versions of 'word' with one deleted letter
   */
  private static ArrayList<String> deletionHelper(String word) {
    ArrayList<String> wordList = new ArrayList<String>();
    // an empty list to which we will add words later
    for (int i = 0; i < word.length(); i++) {
      String delWord = word.substring(0, i) + word.substring(i + 1);
      wordList.add(delWord);
    }
    return wordList;
  }

  /**
   * Generates a list of suggestions using levenshtein
   * edit distance, with the given string and given maximum distance. "Maximum"
   * means words with smaller levenshtein distance will
   * also be in the list of suggestions
   *
   * @param word
   *          any String
   * @param distance
   *          the maximum Levenshtein edit distance
   *
   * @return a list of words which are within the
   *         maximum Levenshtein edit distance from "word"
   */
  public static Set<String> levSuggestions(String word, int distance) {
      Set<String> wordSet = new HashSet<String>();
    // We plan on adding words to the set, since order doesn't matter
    // and we want to avoid duplicates
	  ArrayList<String> tempList = new ArrayList<String>();
	  // there will be no duplicates in tempList, since substitutionHelper,
	  // insertionHelper, and deletionHelper
	  // will each produce a list of unique words of different lengths
	  tempList.addAll(substitutionHelper(word));
	  tempList.addAll(insertionHelper(word));
	  tempList.addAll(deletionHelper(word));
	  for (String word2 : tempList) {
	  	wordSet.add(word2); // words with smaller levenshtein distance than
	                      // the max are to be added as well;
	    wordSet.addAll(levSuggestions(word2, distance - 1));
	  }
    return wordSet;
  }
}