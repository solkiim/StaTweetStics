package edu.brown.cs.StaTweetStics;

import edu.brown.cs.OAuth.Oauth;
import java.io.IOException;
import edu.brown.cs.OAuth.*;
import edu.brown.cs.suggest.*;
import edu.brown.cs.suggest.Graph.*;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

public class Main{

	private String[] args;

	private Main(String[] args){
		this.args = args;
	}
		
	public static void main(String[] args) throws IOException,ClassNotFoundException{
		System.out.println("StaTweetStics");
		if (args.length == 1) {
			//I put in an empty list where competitor handles should go when stuff is implemented
			//Oauth oa = new Oauth(args[0],new ArrayList<String>());
			Oauth.setUser(args[0]);
			Oauth.setCompetitors(new ArrayList<String>());
			Parser<List<Tweet>, Data> par = new TweetDataParser();
			List<Data> res = Oauth.run();
			System.out.println("got data");
			System.out.println(res); 
			Ranker<Word> rank = new TweetRanker(par.parse(res.get(0)));
			List<Word> ranks = rank.rank();
			System.out.println("ranking - part 1");
			NERanker<Word, Tweet> pr = new NERanker<>();
			pr.init(ranks);
			System.out.println("ranking - part 2");
			ranks = pr.rank();
			//System.out.println("done");
			for (Word s : ranks) {
				System.out.println(s.printWordData());
			}
		}
		GUIServer.run(4567);
		
		// List<String> data = new ArrayList<>();
		// Scanner input = new Scanner(System.in);
		// while (input.hasNextLine()){
		// 	String rl = input.nextLine();
		// 	if (rl.equals("")||rl.equals("\n")) {
		// 		break;
		// 	}
		// 	data.add(rl);
			
		// }
		// TweetParser tp = new TweetParser();
		// List<Tweet> tweets = tp.parse(data);
		// Ranker<Word> rank = new TweetRanker(tweets);
		// //PageRanker<Word, Tweet> pr = new PageRanker<>();
		// NERanker<Word, Tweet> pr = new NERanker<>();
		// pr.init(rank.rank());
		// pr.rank();
	}

}