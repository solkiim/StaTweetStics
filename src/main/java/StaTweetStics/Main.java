package edu.brown.cs.StaTweetStics;

import edu.brown.cs.OAuth.Oauth;
import java.io.IOException;
import edu.brown.cs.OAuth.*;
import edu.brown.cs.suggest.*;
import java.util.List;
public class Main{

	private String[] args;

	private Main(String[] args){
		this.args = args;
	}
		
	public static void main(String[] args) throws IOException{
		if (args.length == 1) {
			Oauth oa = new Oauth(args[0]);
			Parser<List<Tweet>, Data> par = new TweetDataParser();
			Data res = oa.run();
			System.out.println(res); 
			Ranker<Word> rank = new TweetRanker(par.parse(res));
			List<Word> ranks = rank.rank();
			for (Word s : ranks) {
				System.out.println(s.printWordData());
			}
		}
		GUIServer.run(4567);
		
		
	}

}