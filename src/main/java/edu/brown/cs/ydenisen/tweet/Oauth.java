package edu.brown.cs.ydenisen.tweet;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.net.ssl.HttpsURLConnection;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import edu.brown.cs.ydenisen.tweet.Data;

public class Oauth {

	private String[] args;
	private static final String CONSUMER_KEY = "dpifh5sWlT348T3grathQxpuD";
	private static final String CONSUMER_SECRET = "pl6J6OnF1Zb7mLdLI3oh69iDOqCmFEkC4HvXczHyD3reaTYNkL";
	private static final String EPU_TOKEN = "https://api.twitter.com/oauth2/token";
	private static final String AE1 = "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=";
	private static final String AE2 = "&count=100&include_rts=false";
	private static final String TREND_ENDPOINT = "https://api.twitter.com/1.1/trends/place.json?id=23424977";

	public Oauth(String[] args){
		this.args = args;
	}

	/**
	* Encode the consumer key and secret
	* @return string of the encoded consumer key and secret
	**/
	private String encodeKeys(String key, String secret){
		try{
			String encodedConsumerKey = URLEncoder.encode(key,"UTF-8");
			String encodedConsumerSecret = URLEncoder.encode(secret,"UTF-8");
			StringBuilder fk = new StringBuilder();
			fk.append(encodedConsumerKey);
			fk.append(":");
			fk.append(encodedConsumerSecret);
			String fullKey = fk.toString();
			byte[] encodedBytes = Base64.encodeBase64(fullKey.getBytes());
			return new String(encodedBytes);
		} catch(UnsupportedEncodingException e){
			return new String();
		}
	}

	/**
	* Construct request for bearer token
	* @param endPointUrl twitter api url
	* @return string of the bearer token if no problems arise
	**/
	private String requestBearerToken(String endPointURL) throws IOException{
		HttpsURLConnection conn = null;
		String creds = encodeKeys(CONSUMER_KEY,CONSUMER_SECRET);
		try{
			URL url = new URL(endPointURL);
			conn = (HttpsURLConnection) url.openConnection();

			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Host","api.twitter.com");
			conn.setRequestProperty("User-Agent","StaTWEETstics");
			conn.setRequestProperty("Authorization","Basic " + creds);
			conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
			conn.setRequestProperty("Content-Length","29");
			conn.setUseCaches(false);
			writeRequest(conn,"grant_type=client_credentials");

			JSONObject o = (JSONObject)JSONValue.parse(readResponse(conn));

			if(o != null){
				String tokenType = (String) o.get("token_type");
				String token = (String) o.get("access_token");
				return ((tokenType.equals("bearer")) && (token != null)) ? token : "";
			}
			return new String();
		} catch(MalformedURLException e){
			throw new IOException("Invalid endpoint URL",e);
		} finally {
			if(conn != null){
				conn.disconnect();
			}
		}
	}

	private JSONArray fetchTimelineTweet(String endPointUrl) throws IOException {
		HttpsURLConnection conn = null;
		try {
			URL url = new URL(endPointUrl); 
			conn = (HttpsURLConnection) url.openConnection();           
			conn.setDoOutput(true);
			conn.setDoInput(true); 
			conn.setRequestMethod("GET"); 
			conn.setRequestProperty("Host", "api.twitter.com");
			conn.setRequestProperty("User-Agent", "StaTWEETstics");
			conn.setRequestProperty("Authorization", "Bearer " + requestBearerToken(EPU_TOKEN));
			conn.setUseCaches(false);

			JSONArray o = (JSONArray)JSONValue.parse(readResponse(conn));
			if (o != null) {
				return o;
			}
			return null;
		} catch (MalformedURLException e) {
			throw new IOException("Invalid endpoint URL.", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}
	
	private JSONArray getTrendingData(String endpointUrl) throws IOException{
		HttpsURLConnection conn = null;
		try{
			URL url = new URL(endpointUrl);
			conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Host","api.twitter.com");
			conn.setRequestProperty("User-Agent","StaTWEETstics");
			conn.setRequestProperty("Authorization","Bearer "+requestBearerToken(EPU_TOKEN));
			conn.setUseCaches(false);
			
			JSONArray o = (JSONArray)JSONValue.parse(readResponse(conn));
			if(o != null){
				return o;
			}
			return null;
		} catch(MalformedURLException e){
			throw new IOException("Invalid endpoint URL.",e);
		} finally{
			if(conn != null){
				conn.disconnect();
			}
		}
	}

	// Writes a request to a connection
	private boolean writeRequest(HttpsURLConnection connection, String textBody) {
		try {
			BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
			wr.write(textBody);
			wr.flush();
			wr.close();	
			return true;
		} catch (IOException e) { 
			return false; 
		}
	}
		
		
	// Reads a response for a given connection and returns it as a string.
	private String readResponse(HttpsURLConnection connection) throws IOException{
		try {
			StringBuilder str = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = "";
			while((line = br.readLine()) != null) {
				str.append(line + System.getProperty("line.separator"));
			}
			return str.toString();
		} catch (IOException e) { 
			e.printStackTrace();
			return new String(); 
		}
	}

	public void run() throws IOException{
		String timeline = AE1+args[0]+AE2;
		JSONArray tweets = fetchTimelineTweet(timeline);
		List<String> timeLineData = new ArrayList<String>(tweets.size());
		for(int i = 0; i < (tweets).size(); i++){
			timeLineData.add((String) ((JSONObject)tweets.get(i)).get("text"));
//			System.out.println(((JSONObject)tweets.get(i)).get("text"));
		}
		JSONArray trending = (getTrendingData(TREND_ENDPOINT));
		JSONArray trends = (JSONArray) ((JSONObject) trending.get(0)).get("trends");
		List<String> trendingData = new ArrayList<String>();
		for(int j = 0; j < trends.size(); j++){
			if(((String)((JSONObject)trends.get(j)).get("name")).startsWith("#")){
				trendingData.add(((String)((JSONObject)trends.get(j)).get("name")));
//				System.out.println(((JSONObject)trends.get(j)).get("name"));
			}
		}
		Data ret = new Data(timeLineData,trendingData);
		System.out.println(ret.toString());
	}

}

