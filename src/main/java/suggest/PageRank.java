
// public class PageRanker implements Ranker<Word> { 
// 	/**
// 		* Default damping factor 
// 		*/ 
// 	public static final double DEFAULT_DAMPING_FACTOR = 0.85; 
	
// 	/**
// 		* Default precision 
// 		*/ 
// 	public static final double DEFAULT_PRECISION = 1.0e-5; 
	
// 	/**
// 		* Current damping factor 
// 		*/ 
// 	protected double dampingFactor; 
	
// 	/**
// 		* Current numeric precision 
// 		*/ 
// 	protected double precision; 
	
// 	/**
// 		* Current rank attribute 
// 		*/ 
// 	protected String rankAttribute; 
	
// 	/**
// 		* Our graph 
// 		*/ 
// 	protected Graph graph; 
	
// 	/**
// 		* Am I up to date ? 
// 		*/ 
// 	protected boolean upToDate; 
	
// 	/**
// 		* The L1 norm of the difference between two consecutive rank vectors 
// 		*/ 
// 	protected double normDiff; 
	
// 	/**
// 		* Used to temporary store the new ranks during an iteration 
// 		*/ 
// 	protected List<Double> newRanks; 
	
// 	/**
// 		* total iteration count 
// 		*/ 
// 	protected int iterationCount; 
	
// 	/**
// 		* Verbose mode 
// 		*/ 
// 	protected boolean verbose; 
	
// 	/**
// 		* Creates a new instance. 
// 		*  
// 		* The damping factor, the precision and the rank attribute are set to their 
// 		* default values 
// 		*/ 
// 	public PageRank() { 
// 		this(DEFAULT_DAMPING_FACTOR, DEFAULT_PRECISION, DEFAULT_RANK_ATTRIBUTE); 
// 	} 
	
// 	/**
// 		* Creates a new instance. 
// 		*  
// 		* @param dampingFactor 
// 		*            Damping factor 
// 		* @param precision 
// 		*            Numeric precision 
// 		* @param rankAttribute 
// 		*            Rank attribute 
// 		*/ 
// 	public PageRank(double dampingFactor, double precision, String rankAttribute) { 
// 		setDampingFactor(dampingFactor); 
// 		setPrecision(precision); 
// 		setRankAttribute(rankAttribute); 
// 		verbose = false; 
// 	} 
	
// 	// parameters 
	
// 	/**
// 		* Returns the current damping factor. 
// 		*  
// 		* @return The current damping factor 
// 		*/ 
// 	public double getDampingFactor() { 
// 		return dampingFactor; 
// 	} 
	
// 	/**
// 		* Sets the damping factor. 
// 		*  
// 		* @param dampingFactor 
// 		*            The new damping factor 
// 		* @throws IllegalArgumentException 
// 		*             If the damping factor is less than 0.01 or greater than 0.99 
// 		*/ 
// 	public void setDampingFactor(double dampingFactor) 
// 			throws IllegalArgumentException { 
// 		if (dampingFactor < 0.01 || dampingFactor > 0.99) 
// 			throw new IllegalArgumentException( 
// 					"The damping factor must be between 0.01 and 0.99"); 
// 		this.dampingFactor = dampingFactor; 
// 		upToDate = false; 
// 	} 
	
// 	/**
// 		* Returns the currently used numeric precision 
// 		*  
// 		* @return The precision 
// 		*/ 
// 	public double getPrecision() { 
// 		return precision; 
// 	} 
	
// 	/**
// 		* Sets the numeric precision. Precision values close to zero lead to more 
// 		* accurate results, but slower convergence 
// 		*  
// 		* @param precision 
// 		*            The new precision 
// 		* @throws IllegalArgumentException 
// 		*             if the precision is less than 1.0e-7 
// 		*/ 
// 	public void setPrecision(double precision) throws IllegalArgumentException { 
// 		if (precision < 1.0e-7) 
// 			throw new IllegalArgumentException("Precision is too small"); 
// 		this.precision = precision; 
// 		upToDate = false; 
// 	} 
	
// 	/**
// 		* Returns the current rank attribute 
// 		*  
// 		* @return The current rank attribute 
// 		*/ 
// 	public String getRankAttribute() { 
// 		return rankAttribute; 
// 	} 
	
// 	/**
// 		* Sets the rank attribute. 
// 		*  
// 		* The computed ranks of each node are stored as values of this attribute. 
// 		*  
// 		* @param rankAttribute 
// 		*            The node attribute used to store the computed ranks 
// 		* @throws IllegalStateException 
// 		*             if the algorithm is already initialized 
// 		*/ 
// 	public void setRankAttribute(String rankAttribute) 
// 			throws IllegalStateException { 
// 		if (graph != null) 
// 			throw new IllegalStateException( 
// 					"this method can be called only before init"); 
// 		this.rankAttribute = rankAttribute; 
// 	} 
	
// 	/**
// 		* Switches on or off the verbose mode. 
// 		*  
// 		* In verbose mode the algorithm prints at each iteration the number of 
// 		* iterations and the L1 norm of the difference between the current and the 
// 		* previous rank vectors. 
// 		*  
// 		* @param verbose 
// 		*            Verbose mode 
// 		*/ 
// 	public void setVerbose(boolean verbose) { 
// 		this.verbose = verbose; 
// 	} 
	
// 	// DynamicAlgorithm implementation 
	
// 	public void init(Graph graph) { 
// 		this.graph = graph; 
// 		graph.addElementSink(this); 
// 		double initialRank = 1.0 / graph.getNodeCount(); 
// 		for (Node node : graph) 
// 			node.addAttribute(rankAttribute, initialRank); 
// 		newRanks = new ArrayList<Double>(graph.getNodeCount()); 
// 		upToDate = false; 
// 		iterationCount = 0; 
// 	} 
	
// 	public void compute() { 
// 		if (upToDate) 
// 			return; 
// 		do { 
// 			iteration(); 
// 			if (verbose) {
// 				System.out.println("%6d%16.8f", iterationCount, normDiff); 
// 			}
// 		} while (normDiff > precision); 
// 		upToDate = true; 
// 	} 
	
// 	public void terminate() { 
// 		graph.removeElementSink(this); 
// 		newRanks.clear(); 
// 		newRanks = null; 
// 		graph = null; 
// 	} 
	
// 	// ElementSink implementation 
	
// 	public void nodeAdded(String sourceId, long timeId, String nodeId) { 
// 		// the initial rank of the new node will be 0 
// 		graph.getNode(nodeId).addAttribute(rankAttribute, 
// 				graph.getNodeCount() == 1 ? 1.0 : 0.0); 
// 		upToDate = false; 
// 	} 
	
// 	public void nodeRemoved(String sourceId, long timeId, String nodeId) { 
// 		// removed node will give equal parts of its rank to the others 
// 		double part = graph.getNode(nodeId).getNumber(rankAttribute) 
// 				/ (graph.getNodeCount() - 1); 
// 		for (Node node : graph) 
// 			if (!node.getId().equals(nodeId)) 
// 				node.addAttribute(rankAttribute, node.getNumber(rankAttribute) 
// 						+ part); 
// 		upToDate = false; 
// 	} 
	
// 	public void edgeAdded(String sourceId, long timeId, String edgeId, 
// 			String fromNodeId, String toNodeId, boolean directed) { 
// 		upToDate = false; 
// 	} 
	
// 	public void edgeRemoved(String sourceId, long timeId, String edgeId) { 
// 		upToDate = false; 
// 	} 
	
// 	public void graphCleared(String sourceId, long timeId) { 
// 		upToDate = true; 
// 	} 
	
// 	public void stepBegins(String sourceId, long timeId, double step) { 
// 	} 
	
// 	// helpers 
	
// 	protected void iteration() { 
// 		double dampingTerm = (1 - dampingFactor) / graph.getNodeCount(); 
// 		newRanks.clear(); 
// 		double danglingRank = 0; 
// 		for (int i = 0; i < graph.getNodeCount(); i++) { 
// 			Node node = graph.getNode(i); 
// 			double sum = 0; 
// 			for (int j = 0; j < node.getInDegree(); j++) { 
// 				Node other = node.getEnteringEdge(j).getOpposite(node); 
// 				sum += other.getNumber(rankAttribute) / other.getOutDegree(); 
// 			} 
// 			newRanks.add(dampingTerm + dampingFactor * sum); 
// 			if (node.getOutDegree() == 0) 
// 				danglingRank += node.getNumber(rankAttribute); 
// 		} 
// 		danglingRank *= dampingFactor / graph.getNodeCount(); 
	
// 		normDiff = 0; 
// 		for (int i = 0; i < graph.getNodeCount(); i++) { 
// 			Node node = graph.getNode(i); 
// 			double currentRank = node.getNumber(rankAttribute); 
// 			double newRank = newRanks.get(i) + danglingRank; 
// 			normDiff += Math.abs(newRank - currentRank); 
// 			node.addAttribute(rankAttribute, newRank); 
// 		} 
// 		iterationCount++; 
// 	} 
	
// 	// results 
	
// 	/**
// 		* Returns the rank of a node. If the ranks are not up to date, recomputes 
// 		* them 
// 		*  
// 		* @param node 
// 		*            A node 
// 		* @return The rank of the node 
// 		*/ 
// 	public double getRank(Node node) { 
// 		compute(); 
// 		return node.getNumber(rankAttribute); 
// 	} 
	
// 	/**
// 		* Returns the total number of iterations. 
// 		*  
// 		* This number accumulates the number of iterations performed by each call 
// 		* to {@link #compute()}. It is reset to zero in the calls to 
// 		* {@link #init(Graph)}. 
// 		*  
// 		* @return The number of iterations 
// 		*/ 
// 	public int getIterationCount() { 
// 		return iterationCount; 
// 	} 
// }