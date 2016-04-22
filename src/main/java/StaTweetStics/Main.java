package edu.brown.cs.ydenisen.tweet;

import edu.brown.cs.ydenisen.tweet.Oauth;
import java.io.IOException;

public class Main{

	private String[] args;

	private Main(String[] args){
		this.args = args;
	}
		
	public static void main(String[] args) throws IOException{
		new Oauth(args).run();
	}

}