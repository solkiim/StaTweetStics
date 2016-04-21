package edu.brown.cs.suggest.Graph;

import java.util.Set;
import java.util.HashSet;
/**
 * this class represents the idea of an edge for a graph or rather that thing
 * that connects vertices. it contains not just two vertices but rather a set
 * of vertex's because in some sences this version of an edge is more abstract.
 **/
public interface Edge<V extends Vertex<V,E>, E extends Edge<V,E>> {
  /**
  * this meathod returns all of the vertices in the edge.
  * @return all the vertices connected by the edge 
  **/
  Set<V> getVertex();
  /**
  * this meathod returns a double representing the weight
  * of the edge.
  * @return a double representing the weight of the edge
  **/
  double getWeight();
  /**
  * this method gets all of the vertices that are not the given vertex
  * @param vertex the vertice that is not going to be returned
  * @return the edge's set of vertices not including vertex
  **/
  default Set<V> getOtherVertex(V vertex) {
    Set<V> vertices = getVertex();
    Set<V> ret = new HashSet<>();
    for (V tmp : vertices) {
      if (!tmp.equals(vertex)) {
        ret.add(tmp);
      }
    }
    return ret;
  }

}
