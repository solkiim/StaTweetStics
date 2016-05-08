package edu.brown.cs.suggest.ORM;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class maintains a connection to a database.
 */
public class Db implements AutoCloseable {
  /** The url to the database. */
  private static String url;
  /** A connection conn.*/
  private Connection conn = null;
  /** local statements.**/
  private Map<String,PreparedStatement> stmts = new HashMap<>();
  /** instances of the db. **/
  private static ThreadLocal<Db> instances = new ThreadLocal<>();
  public Db() {
    conn = buildConnection();
    instances.set(this);
  }
  /**
   * Sets the databse URL.
   *
   * @param newURL The url for this database
   */
  public static void setURL(String newURL) {
    url = "jdbc:sqlite:" + newURL;
    try {
      Class.forName("org.sqlite.JDBC");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  // /**
  //  * Sets this database connection to the connection in the
  //  * input database.
  //  *
  //  * @param db The database to set this BD's conned to
  //  */
  // public void set(Db db) {
  //   this.conned = db.conned;
  // }
  private PreparedStatement prepareHelper(String query) throws SQLException {
    PreparedStatement p = stmts.get(query);
    if (p == null) {
      p = conn.prepareStatement(query);
      stmts.put(query, p);
    }
    return p; 
  }
  private Connection buildConnection() {
    try {
      Connection conned = DriverManager.getConnection(url);
      Statement stmt = conned.createStatement();
      stmt.executeUpdate("PRAGMA foreign_keys = ON;");
      return conned;
    } catch (SQLException se) {
      throw new RuntimeException(se);
    }
  }
  /**
   * This returns the connection to the database.
   *
   * @return the connection to the database
   */
  public static Connection getConnection() {
    assert instances.get() != null : "db does not exist on thread";
    return instances.get().conn;
  }

  public static PreparedStatement prepare(String query) throws SQLException {
    return instances.get().prepareHelper(query);
  }

  /**
   * Commands that need to run through this Db should be Operations.
   */
  public interface Operation<T> {
    /**
     * given a connection returns appropriate value.
     *
     * @param c the connection
     * @return the object that is to be returned
     * @throws SQLException when connection fails
     */
    T executeWith(Connection c) throws SQLException;
  }
  public static String getURL() { return url; }

  /**
   * Closes the connection to db and clears the cache.
   */
  @Override
  public void close() throws SQLException {
    assert instances.get() != null : "db not exist on thread";
    try {
      for (PreparedStatement p : stmts.values()) {
        p.close();
      }
      instances.get().conn.close();
    } finally {
      instances.set(null);
    }
    
    //System.out.println("DB Query Was Closed");
    //cache.clear();
    //throw new Exception("db query was Closed");
  }
}
