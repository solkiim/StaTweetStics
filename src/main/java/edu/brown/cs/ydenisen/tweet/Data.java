package edu.brown.cs.ydenisen.tweet;

import java.util.List;

public class Data {
	
	private List<String> timeLineData;
	private List<String> trendingData;
	
	public Data(List<String> timeLineData, List<String> trendingData){
		this.timeLineData = timeLineData;
		this.trendingData = trendingData;
	}
	
	public List<String> getTimeLineData(){
		return this.timeLineData;
	}
	
	public List<String> getTrendingData(){
		return this.trendingData;
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
