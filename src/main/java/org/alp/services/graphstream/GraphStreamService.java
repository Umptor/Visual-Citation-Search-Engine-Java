package org.alp.services.graphstream;

import org.alp.models.Paper;
import org.alp.services.CrossRefService;
import org.alp.services.CssReader;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.ArrayList;

public class GraphStreamService {
	private final Graph graph = new SingleGraph("Citation Graph");
	private Node root = null;
	private Paper rootPaper = null;

	public GraphStreamService() {
		System.setProperty("org.graphstream.ui", "javafx");
		new OnClickViewerListener(graph, this);
		this.setStyleSheet();
	}

	public Graph getGraph() {
		return graph;
	}

	public void addNode(Paper paper, ArrayList<Paper> edges) {
		if(paper == null || paper.getTitle() == null) {
			return;
		}
		var node = this.addNode(paper);
		if(edges == null) {
			return;
		}

		edges.stream().filter((Paper edge) -> edge.getTitle() != null).map(this::addNode).forEach(secondNode -> {
			String newEdgeId = getEdgeId(node, secondNode);
			if(graph.edges().noneMatch((Edge existingEdge) -> existingEdge.getId().equals(newEdgeId))) {
				Edge edge = graph.addEdge(newEdgeId, node, secondNode);
				edge.setAttribute("isDirected", true);
			}
		});
		paper.getReferences().forEach((Paper reference) -> addNode(reference, reference.getReferences()));
	}


	private Node addNode(Paper paper) {
		Node node = graph.nodes()
				.filter((Node node2) -> node2.getId().equals(paper.getDoi()))
				.findFirst().orElse(null);

		if(node == null) {
			node = graph.addNode(paper.getDoi());
			if(root == null) {
				this.setRoot(node);
				this.rootPaper = paper;
			}
			node.setAttribute("ui.label", "" + paper.getTitle());
		}

		return node;
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

	protected void selectNode(String nodeDoi) {
		Node node = graph.getNode(nodeDoi);
		if(node == null) {
			System.out.println("This isn't a node you dum dum");
		}

		Paper selectedPaper = CrossRefService.findPaper(rootPaper, nodeDoi);

		if(selectedPaper.getPaperAbstract() == null || selectedPaper.getPaperAbstract().equals("")) {
			System.out.println("Paper with DOI: " + selectedPaper.getDoi() + " unfortunately has no abstract in database");
		} else {
			System.out.println("Abstract: " + selectedPaper.getPaperAbstract());
		}


	}

}
