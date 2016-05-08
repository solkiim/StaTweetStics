package edu.brown.cs.StaTweetStics;

import edu.brown.cs.OAuth.Oauth;
import java.io.IOException;
import edu.brown.cs.OAuth.*;
import edu.brown.cs.suggest.*;
import edu.brown.cs.suggest.Graph.*;
import edu.brown.cs.suggest.ORM.*;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;

import java.io.BufferedReader;
import java.io.FileReader;

public class Main{

	private String[] args2;

	private Main(String[] args){
		this.args2 = args;
	}
		
	public static void main(String[] args) throws Exception{
		System.out.println("StaTweetStics");
//<<<<<<< HEAD
//		if (args.length == 1) {
//			//I put in an empty list where competitor handles should go when stuff is implemented
//			//Oauth oa = new Oauth(args[0],new ArrayList<String>());
//			Oauth.setUser(args[0]);
//			Oauth.setCompetitors(new ArrayList<String>());
//			Parser<List<Tweet>, Data> par = new TweetDataParser();
//			List<Data> res = Oauth.run();
//			System.out.println("got data");
//			System.out.println(res); 
//			Ranker<Word> rank = new TweetRanker(par.parse(res.get(0)));
//			List<Word> ranks = rank.rank();
//			System.out.println("ranking - part 1");
//			NERanker<Word, Tweet> pr = new NERanker<>();
//			pr.init(ranks);
//			System.out.println("ranking - part 2");
//			ranks = pr.rank();
//			//System.out.println("done");
//			for (Word s : ranks) {
//				System.out.println(s.printWordData());
//=======
		System.out.println("args:"+Arrays.asList(args));
//		
		System.out.println(Db.getURL());
		if (args.length == 2) {
			Db.setURL(args[args.length-1]);
			User usr = null;
			try (Db db = new Db()) {
				usr = new User(args[0]);
			}
			System.out.println(usr.getTweets());
			return;	
		}
		else if(args.length > 2){
			Db.setURL(args[args.length-1]);
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("--wf")) {
					List<String> arg2 = Arrays.asList(args);
					List<String> arg3 = arg2.subList(i+1,args.length-1);
					String[] args4 = arg3.toArray(new String[0]);
					try (Db db = new Db()) {
						willtest(args4);
					}
					
				}
//>>>>>>> be200a1584989a2766eaa713c74e45ab66121350
			}
		}
		//GUIServer.run(4567);
		
		
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
	private static void willtest(String[] args) {
		System.out.println("willtest:"+Arrays.asList(args));
		StopWords.init();
		if (args.length == 11) {
			
			double alpha = 0, beta = 0,betaB = 0, gamma0 = 0, gamma1 =0;
			int numTopics = 0, numIterations = 0;
			int topWords = 0;
			int topTopics = 0;
			try {
				//0.5 0.01 .01 20 50 100 
				////.25 .5 20 20 18 4000
				topTopics = Integer.parseInt(args[0]);
				topWords = Integer.parseInt(args[1]);
				alpha = Double.parseDouble(args[3]);
				beta = Double.parseDouble(args[4]);
				betaB = Double.parseDouble(args[5]);
				gamma0 = Double.parseDouble(args[6]);
				gamma1 = Double.parseDouble(args[7]);
				numTopics = Integer.parseInt(args[8]);
				numIterations = Integer.parseInt(args[9]);
			} catch (Exception e) {
				System.out.println("Error: args <user> <alpha|d> <beta|d> <topics|int> <iter|int>");
			}
			User usr = new User(args[2]);
			List<User> userList = new ArrayList();
			userList.add(usr);
			System.out.println("ranking - part 1");

			MyLDA4 lda = new MyLDA4(alpha,//alpha, 
				beta,//
				betaB,
				gamma0,
				gamma1,  
				numTopics,//numTopics 
				numIterations,//numIterations
				topWords,//,TopWords,
				userList);//docs);
			//System.out.println("ranking - part 2");
			lda.inference();
			System.out.println("Results");
			int i = 0;
			List<List<List<Tweet>>> usrResults = lda.getTopicsToRank();
			for (List<List<Tweet>> topics : usrResults) {
				for (List<Tweet> topic : topics) {
					if (i > topTopics) {
						continue;
					}
					System.out.println("Topic "+i+": ");
					i++;
					Ranker<Word> rank = new TweetRanker(topic);
					Word.reset(SimilarWords.combineSimilar(Word.cache()));
					List<Word> ranks = rank.rank();
					NERanker<Word, Tweet> pr = new NERanker<>();
					pr.init(ranks);
					ranks = pr.rank();
					int w = 0;
					for (Word s : ranks) {
						if (w >= topWords) {
							continue;
						}
						w++;
						System.out.print("  ");
						System.out.println(s.printWordData());
					}
				}
			}
			System.out.println("printFB");
			lda.printFB(topTopics);
			try{
				lda.outputToFile(args[10]);
			} catch(Exception e) {
				System.out.println("ERROR:");
				throw new RuntimeException(e);
			}
			
			System.out.println("MyLDA4");
			return;
		} 
		// else if (args.length == 1) {
		// 	try(BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
		// 	    String line = br.readLine();
		// 	    int i = 0;
		// 	    while (line != null) {
		// 	    	i++;
		// 	       	//System.out.println(line);
		// 	       	//Oauth.addUserTODb("obama", line, "obamaTest"+i,i, i);
		// 	        line = br.readLine();

		// 	    }
		// 	} catch (IOException e) {
		// 		throw new RuntimeException(e);
		// 	}
		// }
	}
}
// 	private void willtest(String[] args) {
// 		if (args.length == 1) {
// 			Oauth oa = new Oauth(args[0]);
// 			Parser<List<Tweet>, Data> par = new TweetDataParser();
// 			Data res = oa.run();
// 			System.out.println("ranking - part 1");
// 			MyLDA lda = new MyLDA(0.5,//Alpha, 
// 				0.5,//Beta
// 				20,// gamma  
// 				20,//numTopics 
// 				100,//numIterations
// 				1000,//,TopWords, 
// 				0,//SaveStep, 
// 				par.parse(res));//docs);
// 			System.out.println("ranking - part 2");
// 			lda.inference();
// 			System.out.println("Results");
// 			int i = 0;
// 			List<List<Tweet>> topics = lda.getTopicsToRank();
// 			for (List<Tweet> topic : topics) {
// 				System.out.println("Topic 1: ");
// 				Ranker<Word> rank = new TweetRanker(topic);
// 				List<Word> ranks = rank.rank();
// 				System.out.print("  ");
// 				System.out.println("ranking - part 1");
// 				NERanker<Word, Tweet> pr = new NERanker<>();
// 				pr.init(ranks);
// 				System.out.print("  ");
// 				System.out.println("ranking - part 2");
// 				ranks = pr.rank();
// 				//System.out.println("done");
// 				for (Word s : ranks) {
// 					System.out.print("  ");
// 					System.out.println(s.printWordData());
// 				}
// 			}
// 			return;
// 		} else if (args.length == 6) {
			
// 			double alpha = 0, beta = 0, gamma = 0;
// 			int numTopics = 0, numIterations = 0;
// 			try {
// 				alpha = Double.parseDouble(args[1]);
// 				beta = Double.parseDouble(args[2]);
// 				gamma = Double.parseDouble(args[3]);
// 				numTopics = Integer.parseInt(args[4]);
// 				numIterations = Integer.parseInt(args[5]);
// 			} catch (Exception e) {
// 				System.out.println("Error: args <user> <alpha|d> <beta|d> <gamma|d> <topics|int> <iter|int>");
// 			}
// 			Oauth oa = new Oauth(args[0]);
// 			Parser<List<Tweet>, Data> par = new TweetDataParser();
// 			Data res = oa.run();
// 			System.out.println("ranking - part 1");

// 			MyLDA lda = new MyLDA(alpha,//alpha, 
// 				beta,//beta
// 				gamma,// gamma  
// 				numTopics,//numTopics 
// 				numIterations,//numIterations
// 				1000,//,TopWords, 
// 				0,//SaveStep, 
// 				par.parse(res));//docs);
// 			System.out.println("ranking - part 2");
// 			lda.inference();
// 			System.out.println("Results");
// 			int i = 0;
// 			List<List<Tweet>> topics = lda.getTopicsToRank();
// 			for (List<Tweet> topic : topics) {
// 				System.out.println("Topic "+i+": ");
// 				i++;
// 				Ranker<Word> rank = new TweetRanker(topic);
// 				List<Word> ranks = rank.rank();
// 				System.out.print("  ");
// 				System.out.println("ranking - part 1");
// 				NERanker<Word, Tweet> pr = new NERanker<>();
// 				pr.init(ranks);
// 				System.out.print("  ");
// 				System.out.println("ranking - part 2");
// 				ranks = pr.rank();
// 				//System.out.println("done");
// 				for (Word s : ranks) {
// 					System.out.print("  ");
// 					System.out.println(s.printWordData());
// 				}
// 			}
// 			lda.printFB();
// 			System.out.println("MyLDA");
// 			return;
// 		}else if (args.length == 5) {
			
// 			double alpha = 0, beta = 0, gamma = 0;
// 			int numTopics = 0, numIterations = 0;
// 			try {
// 				alpha = Double.parseDouble(args[1]);
// 				beta = Double.parseDouble(args[2]);
// 				numTopics = Integer.parseInt(args[3]);
// 				numIterations = Integer.parseInt(args[4]);
// 			} catch (Exception e) {
// 				System.out.println("Error: args <user> <alpha|d> <beta|d> <topics|int> <iter|int>");
// 			}
// 			Oauth oa = new Oauth(args[0]);
// 			Parser<List<Tweet>, Data> par = new TweetDataParser();
// 			Data res = oa.run();
// 			System.out.println("ranking - part 1");

// 			LDA lda = new LDA(alpha,//alpha, 
// 				beta,//beta  
// 				numTopics,//numTopics 
// 				numIterations,//numIterations
// 				1000,//,TopWords, 
// 				0,//SaveStep, 
// 				par.parse(res));//docs);
// 			System.out.println("ranking - part 2");
// 			lda.inference();
// 			System.out.println("Results");
// 			int i = 0;
// 			List<List<Tweet>> topics = lda.getTopicsToRank();
// 			for (List<Tweet> topic : topics) {
// 				System.out.println("Topic "+i+": ");
// 				i++;
// 				Ranker<Word> rank = new TweetRanker(topic);
// 				List<Word> ranks = rank.rank();
// 				System.out.print("  ");
// 				System.out.println("ranking - part 1");
// 				NERanker<Word, Tweet> pr = new NERanker<>();
// 				pr.init(ranks);
// 				System.out.print("  ");
// 				System.out.println("ranking - part 2");
// 				ranks = pr.rank();
// 				//System.out.println("done");
// 				for (Word s : ranks) {
// 					System.out.print("  ");
// 					System.out.println(s.printWordData());
// 				}
// 			}
// 			lda.printFB();
// 			System.out.println("LDA");
// 			return;
// 		}else if (args.length == 7) {
			
// 			double alpha = 0, beta = 0, gamma0 = 0, gamma1 =0;
// 			int numTopics = 0, numIterations = 0;
// 			try {
// 				alpha = Double.parseDouble(args[1]);
// 				beta = Double.parseDouble(args[2]);
// 				gamma0 = Double.parseDouble(args[3]);
// 				gamma1 = Double.parseDouble(args[3]);
// 				numTopics = Integer.parseInt(args[5]);
// 				numIterations = Integer.parseInt(args[6]);
// 			} catch (Exception e) {
// 				System.out.println("Error: args <user> <alpha|d> <beta|d> <topics|int> <iter|int>");
// 			}
// 			Oauth oa = new Oauth(args[0]);
// 			Parser<List<Tweet>, Data> par = new TweetDataParser();
// 			Data res = oa.run();
// 			System.out.println("ranking - part 1");

// 			MyLDA2 lda = new MyLDA2(alpha,//alpha, 
// 				beta,//beta
// 				gamma0,
// 				gamma1,  
// 				numTopics,//numTopics 
// 				numIterations,//numIterations
// 				1000,//,TopWords, 
// 				0,//SaveStep, 
// 				par.parse(res));//docs);
// 			System.out.println("ranking - part 2");
// 			lda.inference();
// 			System.out.println("Results");
// 			int i = 0;
// 			List<List<Tweet>> topics = lda.getTopicsToRank();
// 			for (List<Tweet> topic : topics) {
// 				System.out.println("Topic "+i+": ");
// 				i++;
// 				Ranker<Word> rank = new TweetRanker(topic);
// 				List<Word> ranks = rank.rank();
// 				System.out.print("  ");
// 				System.out.println("ranking - part 1");
// 				NERanker<Word, Tweet> pr = new NERanker<>();
// 				pr.init(ranks);
// 				System.out.print("  ");
// 				System.out.println("ranking - part 2");
// 				ranks = pr.rank();
// 				//System.out.println("done");
// 				for (Word s : ranks) {
// 					System.out.print("  ");
// 					System.out.println(s.printWordData());
// 				}
// 			}
// 			lda.printFB();
// 			System.out.println("MyLDA2");
// 			return;
// 		}
// 	}
// }

// package edu.brown.cs.StaTweetStics;

// import edu.brown.cs.OAuth.Oauth;
// import java.io.IOException;
// import edu.brown.cs.OAuth.*;
// import edu.brown.cs.suggest.*;
// import edu.brown.cs.suggest.Graph.*;
// import java.util.List;
// import java.util.Scanner;
// import java.util.ArrayList;

// public class Main{

// 	private String[] args;

// 	private Main(String[] args){
// 		this.args = args;
// 	}
		
// 	public static void main(String[] args) throws IOException,ClassNotFoundException{
// 		System.out.println("StaTweetStics");
// 		if (args.length == 1) {
// 			//I put in an empty list where competitor handles should go when stuff is implemented
// 			Oauth oa = new Oauth(args[0],new ArrayList<String>());
// 			Parser<List<Tweet>, Data> par = new TweetDataParser();
// 			List<Data> res = oa.run();
// 			System.out.println("got data");
// 			System.out.println(res); 
// 			Ranker<Word> rank = new TweetRanker(par.parse(res.get(0)));
// 			List<Word> ranks = rank.rank();
// 			System.out.println("ranking - part 1");
// 			NERanker<Word, Tweet> pr = new NERanker<>();
// 			pr.init(ranks);
// 			System.out.println("ranking - part 2");
// 			ranks = pr.rank();
// 			//System.out.println("done");
// 			for (Word s : ranks) {
// 				System.out.println(s.printWordData());
// 			}
// 		}
// 		GUIServer.run(4567);
		
// 		// List<String> data = new ArrayList<>();
// 		// Scanner input = new Scanner(System.in);
// 		// while (input.hasNextLine()){
// 		// 	String rl = input.nextLine();
// 		// 	if (rl.equals("")||rl.equals("\n")) {
// 		// 		break;
// 		// 	}
// 		// 	data.add(rl);
			
// 		// }
// 		// TweetParser tp = new TweetParser();
// 		// List<Tweet> tweets = tp.parse(data);
// 		// Ranker<Word> rank = new TweetRanker(tweets);
// 		// //PageRanker<Word, Tweet> pr = new PageRanker<>();
// 		// NERanker<Word, Tweet> pr = new NERanker<>();
// 		// pr.init(rank.rank());
// 		// pr.rank();
// 	}

// }
