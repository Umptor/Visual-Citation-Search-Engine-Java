package org.alp.services;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

public class GraphStreamService {
	private Graph graph = new SingleGraph("Citation Graph");
	private Node root = null;

	public GraphStreamService() {
		System.setProperty("org.graphstream.ui", "javafx");
		this.setStyleSheet();
	}

	public void addNode(String id, String[] edges) {
		var node = graph.addNode(id);
		if(root == null) this.setRoot(node);

		for(String edge : edges) {
			var secondNode = graph.nodes().filter(nodeForEach ->  nodeForEach.getId().equals(edge)).findFirst().orElse(null);
			if(secondNode == null) {
				secondNode = graph.addNode(edge);
			}
			graph.addEdge(getEdgeId(node, secondNode), node, secondNode);
		}
	}

	public void showGraph() {
		this.graph.display();
	}

	private void setRoot(Node root) {
		this.root = root;
		this.colorRoot();
	}

	private void setStyleSheet() {
		String stylesheet = CssReader.getInstance().getFile();
		graph.setAttribute("ui.stylesheet", stylesheet);
	}

	private void colorRoot() {
		this.root.setAttribute("ui.class", "root");
	}

	private String getEdgeId(Node node1, Node node2) {
		return this.getEdgeId(node1.getId(), node2.getId());
	}

	private String getEdgeId(String node1, String node2) {
		return node1 + node2;
	}
}
