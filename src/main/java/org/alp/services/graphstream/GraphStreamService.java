package org.alp.services.graphstream;

import org.alp.models.Paper;
import org.alp.services.CrossRefService;
import org.alp.services.CssReader;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;

public class GraphStreamService {
	private final Graph graph = new SingleGraph("Citation Graph");
	private final Viewer viewer;
	private final PaperTreeViewerListener listener;
	private Node root = null;
	private Paper rootPaper = null;

	public GraphStreamService() {
		System.setProperty("org.graphstream.ui", "javafx");
		this.setStyleSheet();

		this.viewer = graph.display();
		this.viewer.disableAutoLayout();

		this.viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
		this.listener = new PaperTreeViewerListener(this, graph, viewer);
	}

	public Graph getGraph() {
		return graph;
	}

	public void fillGraph(Paper paper, ArrayList<Paper> edges) {
		this.addNode(paper, edges);
		determineCoordinates();
	}

	public void determineCoordinates() {
		PaperCoordinateGiver.initialize().determineCoordinates(rootPaper);


		Stack<Paper> papers = new Stack<>();
		papers.push(rootPaper);

		while(!papers.isEmpty()) {
			var current = papers.pop();
			var node = this.graph.getNode(current.getDoi());
			if(node == null) {
				continue;
			}
//			current.setZ(0f);
			if(current.getX() == null || current.getY() == null) {
				System.out.println("wtf");
				continue;
			}
			node.setAttribute("xyz", current.getX(), current.getY(), current.getZ());

			if(current.getReferences() != null)
				current.getReferences().stream()
						.filter((Paper paper) -> Objects.nonNull(paper) && paper.getTitle() != null &&
								(paper.getPublishedPrint() != null || paper.getPublishedOnline() != null))
						.collect(Collectors.toList())
						.forEach(papers::push);
		}

	}

	private void addNode(Paper paper, ArrayList<Paper> edges) {
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
				Edge edge = graph.addEdge(newEdgeId, node, secondNode, true);
			}
		});
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

		System.out.println("Date: ");
		System.out.println(selectedPaper.getDay() + "/" + selectedPaper.getMonth() + "/" + selectedPaper.getYear());

		System.out.println(selectedPaper.getX() + " " + selectedPaper.getY() + " " + selectedPaper.getZ());

		// Get Nodes around initialNode
		Paper paper = null;
		boolean success = false;
		try {
			paper = CrossRefService.getMetadata(selectedPaper);
			var references = CrossRefService.getRelatedPapers(paper, 1, true);
			paper.setReferences(references);
			success = true;
		} catch(URISyntaxException | InterruptedException e) {
			e.printStackTrace();
		}

		if(!success) return;


		this.reset();

		this.fillGraph(paper, paper.getReferences());

	}

//	private final Graph graph = new SingleGraph("Citation Graph");
//	private final Viewer viewer;
//	private final PaperTreeViewerListener listener;
//	private Node root = null;
//	private Paper rootPaper = null;

	private void reset() {
		while(this.graph.getNodeCount() > 0) {
			this.graph.removeNode(0);
		}
		while(this.graph.getEdgeCount() > 0) {
			this.graph.removeEdge(0);
		}
	}
}
