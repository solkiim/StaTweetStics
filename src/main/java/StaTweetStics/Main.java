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
		System.out.println("args:"+Arrays.asList(args));
		if(args.length < 1 || args.length > 1){
			System.out.println("Usage: ./run <database>");
		}
		else{
			Db.setURL(args[args.length-1]);
			GUIServer.run(4567);
		} 
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
			User usr = new UserSingle(args[2]);
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
				userList);
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
	}
	private static void willtest2(String[] args) {
		if (args[0].equals("-d")) {
			System.out.println("willtest:"+Arrays.asList(args));
			StopWords.init();
			List<User> userList = new ArrayList();
			for (int l = 4;l < args.length; l++) {
				User usr = new UserSingle(args[l]);
				userList.add(usr);
			}
			int topWords = 0;
			int topTopics = 0;
			try {
				topTopics = Integer.parseInt(args[2]);
				topWords = Integer.parseInt(args[3]);
			} catch (Exception e) {
				System.out.println("Error: args <user> <alpha|d> <beta|d> <topics|int> <iter|int>");
			}
			User usr = new UserSingle(args[2]);
			Word.reset(SimilarWords.combineSimilar(Word.cache()));
			System.out.println("ranking - part 1");

			MyLDA4 lda = new MyLDA4(6,userList);
			lda.inference();
			System.out.println("Results");
			int u = -1;
			int i = 0;
			List<List<List<Tweet>>> usrResults = lda.getTopicsToRank();
			for (List<List<Tweet>> topics : usrResults) {
				u++;
				System.out.println("User "+userList.get(u).getHandle()+": ");
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
				lda.outputToFile(args[1]);
			} catch(Exception e) {
				System.out.println("ERROR:");
				throw new RuntimeException(e);
			}
			System.out.println("MyLDA4");
			return;
		}
		System.out.println("willtest:"+Arrays.asList(args));
		StopWords.init();
		List<User> userList = new ArrayList();
		for (int l = 2;l < args.length; l++) {
			User usr = new UserSingle(args[l]);
			userList.add(usr);
		}
		int topWords = 0;
		int topTopics = 0;
		try {
			topTopics = Integer.parseInt(args[0]);
			topWords = Integer.parseInt(args[1]);
		} catch (Exception e) {
			System.out.println("Error: args <user> <alpha|d> <beta|d> <topics|int> <iter|int>");
		}
		User usr = new UserSingle(args[2]);
		Word.reset(SimilarWords.combineSimilar(Word.cache()));
		System.out.println("ranking - part 1");

		MyLDA4 lda = new MyLDA4(6,userList);
		lda.inference();
		System.out.println("Results");
		int u = -1;
		int i = 0;
		List<List<List<Tweet>>> usrResults = lda.getTopicsToRank();
		for (List<List<Tweet>> topics : usrResults) {
			u++;
			System.out.println("User "+userList.get(u).getHandle()+": ");
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
		System.out.println("MyLDA4");
		return;
	}
	private static void willtest3(String[] args) {
		if (args[0].equals("-d")) {
			System.out.println("willtest:"+Arrays.asList(args));
			StopWords.init();
			List<User> userList = new ArrayList<>();
			List<String> usrHandle = new ArrayList<>();
			for (int l = 4;l < args.length; l++) {
				usrHandle.add(args[l]);
			}
			userList.add(new UserMulti(usrHandle));
			int topWords = 0;
			int topTopics = 0;
			try {
				topTopics = Integer.parseInt(args[2]);
				topWords = Integer.parseInt(args[3]);
			} catch (Exception e) {
				System.out.println("Error: args <user> <alpha|d> <beta|d> <topics|int> <iter|int>");
			}
			User usr = new UserSingle(args[2]);
			System.out.println("ranking - part 1");

			MyLDA4 lda = new MyLDA4(6,userList);
			lda.inference();
			System.out.println("Results");
			int u = -1;
			int i = 0;
			List<List<List<Tweet>>> usrResults = lda.getTopicsToRank();
			for (List<List<Tweet>> topics : usrResults) {
				u++;
				System.out.println("User "+userList.get(u).getHandle()+": ");
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
				lda.outputToFile(args[1]);
			} catch(Exception e) {
				System.out.println("ERROR:");
				throw new RuntimeException(e);
			}
			System.out.println("MyLDA4");
			return;
		}
		System.out.println("willtest:"+Arrays.asList(args));
		StopWords.init();
		List<User> userList = new ArrayList<>();
		List<String> usrHandle = new ArrayList<>();
		for (int l = 4;l < args.length; l++) {
			usrHandle.add(args[l]);
		}
		userList.add(new UserMulti(usrHandle));
		int topWords = 0;
		int topTopics = 0;
		try {
			topTopics = Integer.parseInt(args[0]);
			topWords = Integer.parseInt(args[1]);
		} catch (Exception e) {
			System.out.println("Error: args <user> <alpha|d> <beta|d> <topics|int> <iter|int>");
		}
		User usr = new UserSingle(args[2]);
		System.out.println("ranking - part 1");

		MyLDA4 lda = new MyLDA4(6,userList);
		lda.inference();
		System.out.println("Results");
		int u = -1;
		int i = 0;
		List<List<List<Tweet>>> usrResults = lda.getTopicsToRank();
		for (List<List<Tweet>> topics : usrResults) {
			u++;
			System.out.println("User "+userList.get(u).getHandle()+": ");
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
		System.out.println("MyLDA4");
		return;
	}
}
