
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
/*

Inspired from :

https://github.com/evandempsey/document-summarizer/blob/master/DocumentSummarizer/src/docsum/algorithm/HITSAlgorithm.java


*/
public class HITSAlgorithm {

	public HITSAlgorithm() {

	}

	private HashMap<Integer, HITSNode> makeGraph() {

		HashMap<Integer, HITSNode> graph = new HashMap<Integer, HITSNode>();

		TreeMap<Integer, Integer[]> docmap = new TreeMap<Integer, Integer[]>();
		Integer[] d1 = { 2, 3, 5, 7 };
		Integer[] d2 = { 4, 7 };
		Integer[] d3 = { 1, 5, 8 };
		Integer[] d4 = { 5, 6, 7 };
		Integer[] d5 = { 2, 4, 6, 8 };
		Integer[] d6 = { 2, 3, 5 };
		Integer[] d7 = { 1, 3, 5 };
		Integer[] d8 = { 1, 2, 7 };
		docmap.put(1, d1);
		docmap.put(2, d2);
		docmap.put(3, d3);
		docmap.put(4, d4);
		docmap.put(5, d5);
		docmap.put(6, d6);
		docmap.put(7, d7);
		docmap.put(8, d8);

		// Creating nodes
		for (int i = 1; i <= docmap.size(); i++) {
			graph.put(i, new HITSNode());
		}
		Iterator iter = docmap.entrySet().iterator();

		// adding the edges
		while (iter.hasNext()) {
			Entry e = (Entry) iter.next();
			Integer node = (Integer) e.getKey();
			Integer[] outlinks = (Integer[]) e.getValue();

			System.out.print("D" + node + " :  \n");
			for (int i = 0; i < outlinks.length; i++) {
				System.out.print("\tD" + outlinks[i] + " ");
				graph.get(node).addOutgoing(outlinks[i]);
				graph.get(outlinks[i]).addIncoming(node);
			}

			System.out.println();
		}

		return graph;
	}

	// RUN the HITS algorithm on the graph created so far.
	private void runHITS(HashMap<Integer, HITSNode> graph, int k) {

		int numNodes = graph.size();
		// Arrays for hub and authority scores.
		double[] authorityScores = new double[numNodes+1];
		double[] hubScores = new double[numNodes+1];

		// All scores are initially set to 1.0
		Arrays.fill(authorityScores, 1.0);
		Arrays.fill(hubScores, 1.0);

		// Update authority and hub sequentially for k iterations. k =10 for us.
		for (int i = 1; i <= k; i++) {

			// Keep track of a normalization value.
			double norm = 0.0;

			// Update authority scores.
			for (int j = 1; j < numNodes; j++) {

				// The authority score for a node
				// is the sum of the hub scores of the nodes that point to it.
				double authScore = 0.0;
				for (Integer incoming : graph.get(j).getIncoming()) {
					authScore += hubScores[incoming];
				}

				authorityScores[j] = authScore;
				norm += Math.pow(authScore, 2);
			}

			// Normalize authority scores.
			norm = Math.sqrt(norm);
			for (int j = 1; j < numNodes; j++) {
				authorityScores[j] = authorityScores[j] / norm;
			}

			// Set normalization value back to zero.
			norm = 0.0;

			// Update hub scores.
			for (int j = 1; j < numNodes; j++) {

				// The hub score for a node is the sum
				// of the authority scores of the nodes it points to.
				double hubScore = 0.0;
				for (Integer outgoing : graph.get(j).getOutgoing()) {
					hubScore += authorityScores[outgoing];
				}

				hubScores[j] = hubScore;
				norm += Math.pow(hubScore, 2);
			}

			// Normalize hub scores.
			norm = Math.sqrt(norm);
			for (int j = 1; j <= numNodes; j++) {
				hubScores[j] = hubScores[j] / norm;
			}
		}

		System.out.println("SCORES ");
		System.out.println("authority: ");

		int i = 1;
		for (double d : authorityScores) {
			System.out.print("D" + (i) + "=" + d + " ");
			i++;
		}

		System.out.println("\n\nhub: ");
		i = 1;
		for (double d : hubScores) {
			System.out.print("D" + (i) + "=" + d + " ");
			i++;
		}
	}

	public static void main(String args[]) {
		HITSAlgorithm hits = new HITSAlgorithm();
		HashMap<Integer, HITSNode> nodegraph = new HashMap<Integer, HITSNode>();
		nodegraph = hits.makeGraph();
		hits.runHITS(nodegraph, 10);
	}
}

class HITSNode {

	List<Integer> incoming;
	List<Integer> outgoing;

	/**
	 * Default no-argument constructor that initializes incoming and outgoing
	 * ArrayLists.
	 */
	public HITSNode() {
		incoming = new ArrayList<Integer>();
		outgoing = new ArrayList<Integer>();
	}

	/**
	 * Adds an incoming edge to the node if there is not already an edge from
	 * that node.
	 * 
	 * @param value
	 *            Incoming node index.
	 */
	public void addIncoming(int value) {
		if (!incoming.contains(value)) {
			incoming.add(value);
		}
	}

	/**
	 * Adds an outgoing edge to the node if there is not already an edge to that
	 * node.
	 * 
	 * @param value
	 *            Outgoing node index.
	 */
	public void addOutgoing(int value) {
		if (!outgoing.contains(value)) {
			outgoing.add(value);
		}
	}

	/**
	 * Gets list of incoming edges.
	 * 
	 * @return List of incoming edges.
	 */
	public List<Integer> getIncoming() {
		return incoming;
	}

	/**
	 * Gets list of outgoing edges.
	 * 
	 * @return List of outgoing edges.
	 */
	public List<Integer> getOutgoing() {
		return incoming;
	}
}
