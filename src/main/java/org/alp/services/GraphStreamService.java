package org.alp.services;

import org.alp.models.Paper;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.ArrayList;

public class GraphStreamService {
	private final Graph graph = new SingleGraph("Citation Graph");
	private Node root = null;

	public GraphStreamService() {
		System.setProperty("org.graphstream.ui", "javafx");
		this.setStyleSheet();
	}

	public void addNode(Paper paper, ArrayList<Paper> edges) {
		var node = this.addNode(paper);

		for(Paper edge : edges) {
			var secondNode = graph.nodes()
					.filter((Node nodeForEach) -> nodeForEach.getId().equals(edge.getDoi()))
					.findFirst().orElse(null);

			if(secondNode == null) {
				secondNode = this.addNode(edge);
			}

			String newEdgeId = getEdgeId(node, secondNode);
			if(graph.edges().noneMatch((Edge existingEdge) -> existingEdge.getId().equals(newEdgeId))) {
				graph.addEdge(newEdgeId, node, secondNode);
			}
		}
	}


	private Node addNode(Paper paper) {
		Node node = graph.addNode(paper.getDoi());
		if(root == null) this.setRoot(node);
		node.setAttribute("ui.label", "" + paper.getTitle());

		return node;
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
