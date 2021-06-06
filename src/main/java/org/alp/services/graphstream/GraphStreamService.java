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

	}

	public void determineCoordinates() {

	}

	private void addNode(Paper paper) {
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
	}

	private void reset() {
		while(this.graph.getNodeCount() > 0) {
			this.graph.removeNode(0);
		}
		while(this.graph.getEdgeCount() > 0) {
			this.graph.removeEdge(0);
		}
	}
}
