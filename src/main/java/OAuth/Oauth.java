package edu.brown.cs.OAuth;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import java.io.IOException;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import edu.brown.cs.suggest.ORM.Db;
public class Oauth {

	private static String user;
	private static List<String> competitors = new ArrayList<String>();
	private static String uDB = "";
	private static final String CONSUMER_KEY = "dpifh5sWlT348T3grathQxpuD";
	private static final String CONSUMER_SECRET = "pl6J6OnF1Zb7mLdLI3oh69iDOqCmFEkC4HvXczHyD3reaTYNkL";
	private static final String EPU_TOKEN = "https://api.twitter.com/oauth2/token";
	private static final String AE1 = "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=";
	private static final String AE2 = "&count=200&include_rts=true";
	private static final String AE3 = "&max_id=";
	

	//private constructor for static purposes
	private Oauth(){}
	
	/**
	 * Setter for the user
	 * @param givenUser the user from the args
	 */
	public static void setUser(String givenUser){
		user = givenUser;
	}
	
	/**
	 * Setter for the competitors
	 * @param givenComp the competitors from the args
	 */
	public static void setCompetitors(List<String> givenComp){
		competitors = givenComp;
	}
	
	/**
	 * Setter for user db
	 * @param db user db
	 */
	public static void setUserDB(String db){
		uDB = db;
	}

	/**
	* Encode the consumer key and secret
	* @return string of the encoded consumer key and secret
	**/
	private static String encodeKeys(String key, String secret){
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
	private static String requestBearerToken(String endPointURL) throws IOException{
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
	
	/**
	 * Get timeline data
	 * @param endPointUrl url used to make api call
	 * @return jsonarray of data
	 * @throws IOException
	 */
	private static JSONArray fetchTimelineTweet(String endPointUrl) throws IOException {
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

	// Writes a request to a connection
	private static boolean writeRequest(HttpsURLConnection connection, String textBody) {
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
	private static String readResponse(HttpsURLConnection connection) throws IOException{
		try {
			StringBuilder str = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = "";
			while((line = br.readLine()) != null) {
				str.append(line + System.getProperty("line.separator"));
			}
			return str.toString();
		} catch (IOException e) {
			System.out.println("Please enter a valid Twitter handle");
			 throw new IOException();
		}
//		return new String();
	}
	
	/**
	 * Make a call to the Twitter API to get timeline data
	 * @param tweets resource url with info being requested
	 * @param timeLineData list of text of tweets being updated
	 * @param favoriteCount list of favorite counts for each tweet stored 
	 * @param createdAt list of times each tweet created 
	 */
	private static void oneCall(JSONArray tweets, Connection conn, List<String> timeLineData, 
			List<Integer> favoriteCount, List<String> createdAt){
		for(int i = 0; i < (tweets).size(); i++){
			createdAt.add(((JSONObject)tweets.get(i)).get("created_at").toString());
			long l = Long.parseLong(((JSONObject)tweets.get(i)).get("favorite_count").toString());
			favoriteCount.add((int)l);
			timeLineData.add(((JSONObject)tweets.get(i)).get("text").toString());
		}
		String fill = "INSERT INTO data VALUES(?,?,?,?,?)";
		PreparedStatement prep = null;
		try{
			prep = Db.prepare(fill);
			for(int i = 0; i < tweets.size(); i++){
				prep.setString(1, ((JSONObject)tweets.get(i)).get("text").toString());
				long l = Long.parseLong(((JSONObject)tweets.get(i)).get("favorite_count").toString());
				prep.setInt(2,(int)l);
				long r = Long.parseLong(((JSONObject)tweets.get(i)).get("retweet_count").toString());
				prep.setInt(3,(int)r);
				prep.setString(4,((JSONObject)tweets.get(i)).get("id_str").toString());
				prep.setString(5,user);
				prep.addBatch();
			}
			prep.executeBatch();
		} catch (SQLException e){
			e.printStackTrace();
			System.out.println("ERROR:");
			System.exit(1);
		} 
	}
	
	/**
	 * Make call to API and fill specified db
	 * @param timeLineData
	 * @param favoriteCount
	 * @param createdAt
	 * @param db database to be filled
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private static void execute(List<String> timeLineData, List<Integer> favoriteCount, List<String> createdAt, String db) 
			throws ClassNotFoundException, IOException {
		Class.forName("org.sqlite.JDBC");
		String urlToDb = "jdbc:sqlite:" + db;
		Connection conn = null;
		try{
			conn = DriverManager.getConnection(urlToDb);
		} catch(SQLException e){
			e.printStackTrace();
			System.out.println("ERROR:");
			System.exit(1);
		}
		String timeline1 = AE1+user+AE2;
		JSONArray tweets = fetchTimelineTweet(timeline1);
		oneCall(tweets,conn,timeLineData,favoriteCount,createdAt);
		long lastId = Long.parseLong(((JSONObject)tweets.get(tweets.size()-1)).get("id_str").toString());
		StringBuilder t2 = new StringBuilder();
		t2.append(AE1);
		t2.append(user);
		t2.append(AE2);
		t2.append(AE3);
		t2.append(lastId);
		String timeline2 = t2.toString();
		JSONArray tweets2 = fetchTimelineTweet(timeline2);
		oneCall(tweets2,conn,timeLineData,favoriteCount,createdAt);
		lastId = Long.parseLong(((JSONObject)tweets.get(tweets2.size()-1)).get("id_str").toString());
		t2.replace(timeline2.length()-(String.valueOf(lastId)).length(), timeline2.length(), String.valueOf(lastId));
		JSONArray tweets3 = fetchTimelineTweet(timeline2);
		oneCall(tweets3,conn,timeLineData,favoriteCount,createdAt);
		
		try{
			if(conn != null){
				conn.close();
			}
		} catch (SQLException e){
			e.printStackTrace();
			System.out.println("ERROR:");
			System.exit(1);
		}
	}


	public static List<Data> run() throws IOException, ClassNotFoundException {
		// *********************USER STUFF HAPPENING***************************
		List<String> timeLineData = new ArrayList<String>();
		List<Integer> favoriteCount = new ArrayList<Integer>();
		List<String> createdAt = new ArrayList<String>();
		execute(timeLineData,favoriteCount,createdAt,uDB);
		Data userData = new Data(timeLineData,favoriteCount,createdAt);
		
		// *********************COMPETITOR STUFF HAPPENING***************************
		List<String> comptlData = new ArrayList<String>();
		List<Integer> compFaveCount = new ArrayList<Integer>();
		List<String> compCreatedAt = new ArrayList<String>();
		for(int i = 0; i < competitors.size(); i++){
			execute(comptlData,compFaveCount,compCreatedAt,uDB);
		}
		Data compData = new Data(comptlData,compFaveCount,compCreatedAt);
		
		List<Data> ret = new ArrayList<Data>(2);
		ret.add(userData);
		ret.add(compData);
		return ret;
	}

}
