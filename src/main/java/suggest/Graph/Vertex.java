package edu.brown.cs.suggest.Graph;

import java.util.Set;
/**
 * this class represents the idea of the vertex a graph.
**/
public interface Vertex<V extends Vertex<V,E>, E extends Edge<V,E>> {
  /**
  * this method returns the edges that connect this vertex to other vertices
  * @return the set of edges for this vertex
  **/
  Set<E> getEdges();
  double nodeWeight();
}
