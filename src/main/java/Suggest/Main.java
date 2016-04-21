package edu.brown.cs.wflotte.termproject;
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
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;


import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import com.google.common.collect.ImmutableList;

public class Main {
  private static final Splitter MY_SPLITTER = Splitter.on(CharMatcher.WHITESPACE).trimResults().omitEmptyStrings();
  /** the given argumens.*/
  /**
  *this is the main meathod
  *@param args is an array of strings which are
  * the delimited arguments from the terminal
  */
  public static void main(String[] args) {
    List<String> rep = new ImmutableList.Builder<String>()
      .add(",").add(".").add("&").add(";").add(":")
      .add("\"").add("\'").add("!").add("$").add("%")
      .add("*").add("<").add(">").add("{").add("}").add("[")
      .add("]").add("\\").build();

    try(BufferedReader br = new BufferedReader(new FileReader(args[1]))) {
      StringBuilder sb = new StringBuilder();
      String line = br.readLine();
      for (String r : rep) {
        line.replace(r,"");
      }
      Suggestor<String> sug = new TweetSuggestor();
      //List<Tweet> tweets = new ArrayList<>();
      while (line != null) {
        sug.parse(line);
        line = br.readLine();
      }
      
      int num = Integer.valueOf(args[0]);
      List<String> s = sug.rank(num);
      for (String str : s) {
        System.out.println(str);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
