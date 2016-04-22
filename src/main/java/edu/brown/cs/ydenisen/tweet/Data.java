package edu.brown.cs.ydenisen.tweet;

import java.util.List;

public class Data {
	
	private List<String> timeLineData;
	private List<Integer> favoriteCount;
	private List<String> trendingData;
	
	public Data(List<String> timeLineData, List<String> trendingData, List<Integer> favoriteCount){
		this.timeLineData = timeLineData;
		this.favoriteCount = favoriteCount;
		this.trendingData = trendingData;
	}
	
	public List<String> getTimeLineData(){
		return this.timeLineData;
	}
	
	public List<String> getTrendingData(){
		return this.trendingData;
	}
	
	public List<Integer> getFavoriteCount(){
		return this.favoriteCount;
	}
	
	@Override 
	public String toString(){
		StringBuilder s = new StringBuilder();
		s.append("Timeline Data");
		s.append("\n");
		for(String tl : this.timeLineData){
			s.append(tl);
			s.append(" | ");
		}
		s.append("\n");
		s.append("Trending Data");
		s.append("\n");
		for(String t : this.trendingData){
			s.append(t);
			s.append(" | ");
		}
		return s.toString();
	}

}
