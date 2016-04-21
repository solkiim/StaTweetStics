package Suggest;
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
import java.util.Objects;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.Multiset;
import com.google.common.collect.HashMultiset;


public class Word {
	private List<Tweet> tweets;
	private String word;
	public Word(String word,List<Tweet> tweets){
		this.tweets = tweets;
		this.word = word;
	}
	public List<Tweet> getTweets(){
		return tweets;
	}
	public String getWord(){
		return word;
	}
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}
		// object must be Test at this point
		Word test = (Word)obj;
		return word.equals(test.word);
	}
	public int hashCode(){
		return Objects.hash(word);
	}
}