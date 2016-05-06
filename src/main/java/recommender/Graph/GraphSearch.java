package edu.brown.cs.suggest.Graph;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.Objects;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
/**
* this class is an abstract class that does the
* searching of the graph via static meathods.
* this makes sence because searching isn't really
* an object but more of a meathod in my mind.
*/
public abstract class GraphSearch {
  /**
  * this is a private class that represents the important info
  * in the priority queue and makes for easy to reconstruct paths
  */
  private static class PQNode<V extends Vertex<V,E>, E extends Edge<V,E>> {
    /** the primary vert.*/
    private final V vert;
    /** the edge that connects the vert to prev. */
    private E edge;
    /** the cost upto this point.*/
    private double cost;
    /** the previous linkup in the node.*/
    private PQNode<V,E> prev;
    /**
    * this is the constructor.
    * @param vertex the primary vertex
    * @param e the connecting edge
    * @param prevVal is the previus PQNode
    * @param costSoFar the cost upto this point
    */
    public PQNode(V val,E e, PQNode<V,E> prevVal, double costSoFar) {
      vert = val;
      edge = e;
      prev = prevVal;
      cost = costSoFar; 
    }
    /**
    * returns the primary vert.
    * @return the primary vert
    */
    public V vert() {
      return vert;
    }
    /**
    * returns the previous pqnode.
    * @return the previous pqnode
    */
    public PQNode<V,E> prev() {
      return prev;
    }
    /**
    * returns the cost so far.
    * @return the value of coste
    */
    public double cost() {
      return cost;
    }
    /**
    * returns the edge connecting the prev to vert.
    * @return the value of edge
    */
    public E edge() {
      return edge;
    }
    /**
    * this sets the previous node and all associated values
    * @param e the new edge
    * @param pv the previous pqnode
    * @param c the new cost
    */
    public void setPrev(E e, PQNode<V,E> pv, double c) {
      edge = e;
      prev = pv;
      cost = c;
    }
    @Override
    public boolean equals(Object o) { 
        if (o == this) {
            return true;
        }
        if (!(o instanceof PQNode)) {
            return false;
        }
        PQNode<V,E> pq = (PQNode<V,E>) o;
         
        // Compare the data members and return accordingly 
        return vert.equals(pq.vert());
    }
    public String toString() {
      return "{V:"+vert()+",cost:"+cost+",edge:"+edge+"}";
    }
    @Override
    public int hashCode() {
      return vert.hashCode();
    }
  }
  /**
  * this is the interface that is used to specify special conditions for
  * the user's seach
  */
  public static interface SpecificCasesHandler <V,E> {
    /**
    * returns true if meets requirements.
    * @param current the current vertex
    * @param prev the previous vertex
    * @param connect the edg conneting the current vertex
    * to the previous vertex
    * @param prevEdge the edge before connect
    * @return true if meets special requierments false otherwise
    */
    boolean isOkay(V current,V prev,E connect,E prevEdge);
  }
  /**
  * this given a starting vertex and an ending vertex and a specificCase handler
  * returns the shortest path via Paths.
  *@param start the starting point
  *@param end the ending point
  *@param sch the specific case handler
  *@return the an empty list if no connection otherwise a list of path connections
  * inorder from start to end.
  */
  public static <V extends Vertex<V,E>, E extends Edge<V,E>> 
  List<Path<V,E>> shortestPath(V start, V end, SpecificCasesHandler<V,E> sch) {
    List<Path<V,E>> resultList = new ArrayList<>();
    if (start == null || end == null) {
      return resultList;
    }
    PQNode<V,E> res = shortestPathHelper(start, end,sch);
    if(res == null) { return resultList;}
    PQNode<V,E> prev = null;
    while ((prev = res.prev()) != null) {
        Path<V,E> pc =
         new Path<>(prev.vert(),res.vert(),res.edge());
        resultList.add(pc);
        //System.out.println(pc);
      res = res.prev();
    }
    Collections.reverse(resultList);
    return resultList;
  }
  /**
  * this is the helper method that implements the dijkstras algorithm of
  * the shortest path helper.
  *@param start the starting point
  *@param end the ending point
  *@param sch the specific case handler
  *@return the last PQNode for the list or null if no list 
  */
  private static <V extends Vertex<V,E>, E extends Edge<V,E>>
  PQNode<V,E> shortestPathHelper(V start, V end,SpecificCasesHandler<V,E> sch) {
    PriorityQueue<PQNode<V,E>> pq = new PriorityQueue<>(5, (a,b) -> {
      double ac = a.cost();
      double bc = b.cost();
      if(ac < bc) {
        return -1;
      }
      if(ac > bc) {
        return 1;
      }
      return 0;
    });

    Map<V, PQNode<V,E>> visited = new HashMap<>();
    PQNode<V,E> begin = new PQNode<>(start,null,null,0);
    pq.add(begin);
    visited.put(start,begin);

    while (!pq.isEmpty()) {
      PQNode<V,E> pqnode = pq.poll();
      V vertex = pqnode.vert();
      if (end.equals(vertex)) {
        return pqnode;
      }
      
      Set<E> edges = vertex.getEdges();
      for (E e : edges) {
        Set<V> others = e.getOtherVertex(vertex);
        for (V v : others) {
          if (sch.isOkay(v,pqnode.vert(),e,pqnode.edge())) {
            PQNode<V,E> res = visited.get(v);
            if(res == null) {
              PQNode<V,E> newPqNode = new PQNode<>(v,e,pqnode,e.getWeight() + pqnode.cost());
              pq.add( newPqNode );
              visited.put(v,newPqNode);
            } else {
              double val = e.getWeight() + pqnode.cost();
              if(val < res.cost()) {
                PQNode<V,E> newPqNode = new PQNode<>(v,e,pqnode,val);
                visited.put(v,newPqNode);
                pq.add( newPqNode );

              }
            }
          } 
        }
      }
    }
    return null;
  }
}
