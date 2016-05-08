package edu.brown.cs.suggest.Graph;
/**
* this class represents the idea of a connection from a starting point
* to an ending. and also the edge that connects them. this class makes it easy
* to get the results from the GraphSearch Class.
**/
public class Path<V extends Vertex<V,E>, E extends Edge<V,E>> {
    /** this is the beginning point of the connection.**/
    private final V begin;
    /** this is the end point of the connecetion.**/
    private final V end;
    /** this is the edge that connects the beginning and the end.**/
    private final E connect;
    /**
    * this is the construtor for a Path.
    * @param start the beginning point for the connection.
    * @param stop this is the ending point for the connection
    * @param conn the connection that links the previous two.
    **/
    public Path(V start,V stop,E conn) {
      begin = start;
      end = stop;
      connect = conn;
    }
    /**
    * this method returns the beginning point.
    * @return the beginning point
    **/
    public V begin() { return begin; }
    /**
    * this method returns the ending point.
    * @return the ending point
    **/
    public V end() { return end; }
    /**
    * this method returns the edge between the beginning and ending point.
    * @return the connectiing edge between the two point
    **/
    public E connect() { return connect; }
    /**
    * this method returns the string representtation of the connection.
    *@return the string representation
    */
    @Override
    public String toString() { 
      return "s: "+begin+ " e: " + end + " : " + connect;
    }
    @Override
    public boolean equals(Object o) {
    try {
      Path obj = (Path) o;
      return (begin.equals(obj.begin()) && end.equals(obj.end()) && 
        connect.equals(obj.connect()));
    } catch (ClassCastException cce){
      return false;
    }
  }
}
