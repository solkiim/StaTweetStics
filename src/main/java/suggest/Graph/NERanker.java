package edu.brown.cs.suggest.Graph;
import java.util.Arrays;
import com.google.common.base.Splitter;
import com.google.common.base.CharMatcher;
import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import com.google.common.collect.Multiset;
import com.google.common.collect.HashMultiset;
import java.util.PriorityQueue;
import java.util.Queue;
import edu.brown.cs.suggest.Ranker;
import java.util.Collections;


public class NERanker<V extends Vertex<V,E>, E extends Edge<V,E>>
 implements Ranker<V> { 
	/**
	* this is a private class that represents the important info
	* for page ranking the data
	*/
	private static class PRNode<V extends Vertex<V,E>, E extends Edge<V,E>>
			 implements Comparable<PRNode<V,E>> {
		/** the primary vert.*/
		private final V vert;
		/** the cost upto this point.*/
		private double cost;
		private double newRank = 0;
		private Set<V> others = null;
		private double outDegree = -1;
		private double allEdgeWeights = -1;
		/**
		* this is the constructor.
		* @param vertex the primary vertex
		* @param e the connecting edge
		* @param prevVal is the previus PQNode
		* @param costSoFar the cost upto this point
		*/
		public PRNode(V vert, double cost) {
			this.vert = vert;
			this.cost = cost; 
		}
		/**
		* returns the primary vert.
		* @return the primary vert
		*/
		public V vert() {
			return vert;
		}
		public Set<V> others() {
			if (others == null) {
				Set<V> newOthers = new HashSet<>();
				for (E edge : vert.getEdges()) {
					newOthers.addAll(edge.getOtherVertex(vert));
				}
				others = newOthers;
			}
			return others;
		}
		public double outDegree() {
			if (outDegree < 0) {
				outDegree = (double) others().size();
			}
			return outDegree;
		}
		public double allEdgeWeights() {
			if (allEdgeWeights < 0) {
				double newAEW = 0;
				for (E edge : vert.getEdges()) {
					//for (V ver : edge.getVertex()) {
						newAEW += (edge.getWeight()+1.0)*edge.getVertex().size();
					//}
				}
				allEdgeWeights = newAEW;
			}
			return allEdgeWeights;
		}
		/**
		* returns the cost so far.
		* @return the value of coste
		*/
		public double cost() {
			return cost;
		}
		public double newRank() {
			return newRank;
		}
		/**
		* returns the cost so far.
		* @return the value of coste
		*/
		public void setCost(double cost) {
			this.cost = cost;
		}
		public void setNewRank(double cost) {
			this.newRank = cost;
		}
		@Override
		public boolean equals(Object o) { 
				if (o == this) {
						return true;
				}
				if (!(o instanceof PRNode)) {
						return false;
				}
				PRNode<V,E> pq = (PRNode<V,E>) o;
				 
				// Compare the data members and return accordingly 
				return vert.equals(pq.vert());
		}
		public int compareTo(PRNode<V,E> pr) {
			return Double.compare(pr.cost(),this.cost());
		}
		public String toString() {
			return "{V:"+vert()+",cost:"+cost+",newRank:"+newRank+"}";
		}
		@Override
		public int hashCode() {
			return vert.hashCode();
		}
	}
	/** Default damping factor. */ 
	public static final double DEFAULT_DAMPING_FACTOR = 0.85; 
	/** Default precision. */ 
	public static final double DEFAULT_PRECISION = 0.00001;//1.0e-5;
	public static final boolean DEBUG_OUTPUT = true;
	/** Current damping factor. */ 
	protected double dampingFactor; 
	/** Current numeric precision. */ 
	protected double precision;
	/** if pagerank is up-to-date*/
	protected boolean upToDate; 	
	/** The L1 norm of the difference between two consecutive rank vectors*/ 
	protected double normDiff; 
	/** Used to temporary store the new ranks during an iteration */ 
	protected List<Double> newRanks; 
	/** total iteration count. */ 
	protected int iterationCount;
	/** the set of all vertices.*/
	protected Map<V,PRNode<V,E>> vertices;
	//protected Set<PRNode<V,E>> vertices;
	protected double verticesSize;

	public NERanker() { 
		this(DEFAULT_DAMPING_FACTOR, DEFAULT_PRECISION); 
	}
	public NERanker(double dampingFactor, double precision) { 
		setDampingFactor(dampingFactor); 
		setPrecision(precision); 
	} 
	public double getDampingFactor() { 
		return dampingFactor; 
	} 
	public void setDampingFactor(double dampingFactor) 
			throws IllegalArgumentException { 
		if (dampingFactor < 0.01 || dampingFactor > 0.99) 
			throw new IllegalArgumentException( 
					"The damping factor must be between 0.01 and 0.99"); 
		this.dampingFactor = dampingFactor; 
		upToDate = false; 
	}
	public double getPrecision() { 
		return precision; 
	}  
	public void setPrecision(double precision) 
			throws IllegalArgumentException { 
		if (precision < 1.0e-7) 
			throw new IllegalArgumentException("Precision is too small"); 
		this.precision = precision; 
		upToDate = false; 
	}
	public void init(List<V> vertexs) { 
		verticesSize = (double) vertexs.size();
		//this.vertices = vertices; 
		//graph.addElementSink(this); 
		vertices = new HashMap<>();
		double initialRank = 1.0 / verticesSize; 
		for (V vert : vertexs) {
			vertices.put(vert,new PRNode<V,E>(vert,initialRank)); 
		}
		//newRanks = new ArrayList<Double>(graph.getNodeCount()); 
		upToDate = false; 
		iterationCount = 0; 
	}
	public void compute() { 
		if (upToDate) 
			return; 
		do { 
			iteration(); 
			if (DEBUG_OUTPUT) {
				//System.out.println(String.format("iter %6d%16.8f", iterationCount, normDiff)); 
			}
		} while (normDiff > precision); 
		upToDate = true;
	}

	protected void iteration() { 
		double dampingTerm = (1 - dampingFactor);
		double danglingRank = 0; 
		for (PRNode<V,E> node : vertices.values()) {  
			double sum = 0;

			//Set<V> others = node.others();
			for (E edge : node.vert().getEdges()) {
				for (V otherKey : edge.getVertex()) {
					if (!otherKey.equals(node.vert())) {
						PRNode<V,E> other = vertices.get(otherKey);
						//System.out.println(other+" allEdgeWeights "+other.allEdgeWeights()+" outDegree: "+other.outDegree()+" edge w: "+edge.getWeight());
				 		sum += other.cost() * (edge.getWeight()/(other.allEdgeWeights()));
				 		//System.out.println("sum "+sum);
			 		}
				}
			}
			

			node.setNewRank(dampingTerm * node.vert().nodeWeight() + dampingFactor * sum * node.vert().nodeWeight());
			//System.out.println("vert: "+node+" sum "+sum+" nodescore "+node.vert().nodeWeight()); 
			if (node.outDegree() == 0) {
				danglingRank += node.cost(); 
			}
		}
		//System.out.println("damping factor: "+dampingFactor+" size "+vertices.size()+" danglingRank: "+danglingRank);
		double numOfVert = (double) vertices.size();
		danglingRank *= dampingFactor / numOfVert;
		//System.out.println("damping factor: "+dampingFactor+" size "+numOfVert+" danglingRank: "+danglingRank);
		normDiff = 0; 
		for (PRNode<V,E> node : vertices.values()) { 
			//Node node = graph.getNode(i); 
			double currentRank = node.cost(); 
			double newRank = node.newRank() + danglingRank; 
			normDiff += Math.abs(newRank - currentRank);
			node.setCost(newRank); 
			//node.addAttribute(rankAttribute, newRank); 
		} 
		iterationCount++; 
	}

	public List<V> rank() {
		compute();
		List<PRNode<V,E>> l = new ArrayList<>(vertices.size());
		l.addAll(vertices.values());
		Collections.sort(l);
		List<V> result = new ArrayList<>(vertices.size());
		for (PRNode<V,E> pr : l) {
			//System.out.println(pr);
			result.add(pr.vert());
		}
		return result;
	}
}