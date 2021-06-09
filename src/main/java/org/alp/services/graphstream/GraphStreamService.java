package org.alp.services.graphstream;

import org.alp.models.Paper;
import org.alp.services.CrossRefService;
import org.alp.services.CssReader;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.view.Viewer;

public class GraphStreamService {
	private final Graph graph = new SingleGraph("Citation Graph");
	private Node root = null;
	private Paper rootPaper = null;

	public GraphStreamService() {
		System.setProperty("org.graphstream.ui", "javafx");
		this.setStyleSheet();

		Viewer viewer = graph.display();
		viewer.disableAutoLayout();

		viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
		new PaperTreeViewerListener(this, graph, viewer);
	}

	public void showGraph(Paper root) {
		var paperCoordinateGiver = PaperCoordinateGiver.initialize();
		paperCoordinateGiver.determineCoordinates(root);

		addNode(root);
		root.getReferences().forEach(this::addNode);

		addEdges(root);
	}

	private void addNode(Paper paper) {
		Node node = this.graph.addNode(paper.getDoi());

		if(this.rootPaper == null) {
			this.setRoot(node);
			this.rootPaper = paper;
		}

		node.setAttribute("ui.label", "" + paper.getTitle());
		node.setAttribute("xyz", paper.getX(), paper.getY(), paper.getZ());
	}

	private void addEdges(Paper root) {
		root.getReferences().forEach(reference -> this.graph.addEdge(
				getEdgeId(root, reference),
				this.graph.getNode(root.getDoi()),
				this.graph.getNode(reference.getDoi()),
				true));
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

	private String getEdgeId(Paper node1, Paper node2) {
		return this.getEdgeId(node1.getDoi(), node2.getDoi());
	}

	private String getEdgeId(String node1, String node2) {
		return node1 + node2;
	}

	protected Point3 selectNode(String nodeDoi) {
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

		if(selectedPaper == rootPaper) return new Point3(selectedPaper.getX(), selectedPaper.getY(), selectedPaper.getZ());

		return getNewRoot(selectedPaper, rootPaper);
	}

	private Point3 getNewRoot(Paper newRoot, Paper oldRoot) {
		// Get Nodes around initialNode
		Paper newRootWithReferences = CrossRefService.getFullReferences(newRoot, oldRoot);

		assert newRootWithReferences != null;

		this.reset();

		this.showGraph(newRootWithReferences);

		return new Point3(newRootWithReferences.getX(), newRootWithReferences.getY(), newRootWithReferences.getZ());
	}

	private void reset() {
		while(this.graph.getNodeCount() > 0) {
			this.graph.removeNode(0);
		}
		while(this.graph.getEdgeCount() > 0) {
			this.graph.removeEdge(0);
		}

		this.rootPaper = null;
		this.root = null;
	}
}
