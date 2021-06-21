package org.alp.services.graphstream;

import org.alp.models.Paper;
import org.alp.services.PaperService;
import org.alp.services.CssReader;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.stylesheet.Color;
import org.graphstream.ui.view.Viewer;

import java.util.ArrayList;

public class GraphStreamService {
	private final Graph graph = new SingleGraph("Citation Graph");
	private Node root = null;
	private Paper rootPaper = null;
	private final ArrayList<Color> colorScheme = new ArrayList<>();

	{
		colorScheme.add(new Color(224, 242, 216));
		colorScheme.add(new Color(205, 234, 195));
		colorScheme.add(new Color(164, 223, 182));
		colorScheme.add(new Color(112, 204, 197));
		colorScheme.add(new Color(75, 180, 211));
	}


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
		colorNodes(root);
	}

	private void addNode(Paper paper) {
		Node node = this.graph.addNode(paper.getDoi());

		if(this.rootPaper == null) {
			this.setRoot(node);
			this.rootPaper = paper;
		}
		String label = paper.getTitle().substring(0, Math.min(paper.getTitle().length(), 50));

		node.setAttribute("ui.label", "" + label);
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

		Paper selectedPaper = PaperService.findPaper(rootPaper, nodeDoi);

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

	public void printXAndY(String doi) {
		Paper selectedPaper = PaperService.findPaper(rootPaper, doi);
		System.out.println("X from GraphStream = " + selectedPaper.getX());
		System.out.println("Y from GraphStream = " + selectedPaper.getY());
	}

	public Paper getPaper(String doi) {
		return PaperService.findPaper(rootPaper, doi);
	}


	private Point3 getNewRoot(Paper newRoot, Paper oldRoot) {
		// Get Nodes around initialNode
		Paper newRootWithReferences = PaperService.getFullReferences(newRoot, oldRoot);

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

	private void colorNodes(Paper root) {
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;

		ArrayList<Paper> papers = new ArrayList<>(root.getReferences());
		papers.add(root);

		for(Paper paper : papers) {
			min = Math.min(min, paper.getReferences().size());
			max = Math.max(max, paper.getReferences().size());
		}

		int[] colorCuttoffs = getColorCutoffs(min, max);


		papers.forEach(paper -> {
			Color color = calculateColor(paper, colorCuttoffs);
			Node node = graph.getNode(paper.getDoi());

			node.setAttribute("ui.style", "fill-color: rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ");");
		});
	}

	private int[] getColorCutoffs(int min, int max) {
		int[] colorCutoffs = new int[]{min, 0, (min + max)/2, 0, max};
		colorCutoffs[1] = (colorCutoffs[0] + colorCutoffs[2])/2;
		colorCutoffs[3] = (colorCutoffs[4] + colorCutoffs[2])/2;

		return colorCutoffs;
	}

	private Color calculateColor(Paper paper, int[] colorCutoffs) {
		int referenceCount = paper.getReferences().size();
		Color color = colorScheme.get(colorCutoffs.length - 1);

		for(int i = 0; i < colorCutoffs.length; i++) {
			if(referenceCount <= colorCutoffs[i]) {
				color = colorScheme.get(i);
				break;
			}
		}

		return color;
	}

}
