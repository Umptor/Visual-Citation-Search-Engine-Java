package org.alp.services.drawer;

import javafx.collections.ObservableList;
import javafx.scene.*;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.alp.App;
import org.alp.models.rectangles.Coordinates;
import org.alp.models.Paper;
import org.alp.models.rectangles.PaperRectangle;
import org.alp.models.rectangles.PaperText;
import org.alp.services.graphstream.PaperCoordinateGiver;

import java.util.*;
import java.util.stream.Collectors;


public class GraphDrawer {
	private final Pane graphPane;
	private final PaperCoordinateGiver coordinateGiver = PaperCoordinateGiver.initialize();

	private final Map<String, ArrayList<PaperRectangle>> nodesFromEdgeId = new HashMap<>();
	private final Map<String, Line> lineFromEdgeIdMap = new HashMap<>();
	private final ArrayList<PaperRectangle> papers = new ArrayList<>();

	private PaperRectangle root;

	public static double height = 100.0;
	public static double width = 300.0;

	private double dragStartX = 0.0;
	private double dragStartY = 0.0;
	private final double minDistanceForDrag = 2.0;
	private boolean cursorNormal = true;

	private ContextMenu contextMenu;
	private final Scene scene;

	private final ArrayList<Color> colorScheme = new ArrayList<>();

	{
		colorScheme.add(Color.rgb(224, 242, 216));
		colorScheme.add(Color.rgb(205, 234, 195));
		colorScheme.add(Color.rgb(164, 223, 182));
		colorScheme.add(Color.rgb(112, 204, 197));
		colorScheme.add(Color.rgb(75, 180, 211));
	}

	Map<Coordinates, PaperRectangle> drawNodes = new HashMap<>();

	public GraphDrawer(Pane graphPane, Scene scene) {
		this.graphPane = graphPane;
		this.scene = scene;

		this.setupEventHandlers();
		this.setupContextMenu();
	}

	private void setupEventHandlers() {
		this.graphPane.addEventHandler(MouseDragEvent.MOUSE_PRESSED, this::onMouseDown);
		this.graphPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::onMouseDrag);
		this.graphPane.addEventHandler(MouseEvent.MOUSE_RELEASED, this::onMouseUp);
	}

	private void setupContextMenu() {
		ContextMenu contextMenu = new ContextMenu();
		MenuItem menuItem1 = new MenuItem("Choice 1");
		MenuItem menuItem2 = new MenuItem("Choice 2");
		MenuItem menuItem3 = new MenuItem("Choice 3");

		menuItem3.setOnAction((event) -> System.out.println("Choice 3 clicked!"));
		contextMenu.getItems().addAll(menuItem1,menuItem2,menuItem3);
		this.contextMenu = contextMenu;
	}

	public void drawGraph(Paper root) {
		this.reset();
		coordinateGiver.determineCoordinates(root);
		List<Paper> papers = new ArrayList<>();
		papers.add(root);
		papers.addAll(root.getReferences());

		List<PaperRectangle> paperRectangles = papers.stream()
				.filter(Objects::nonNull).filter(paper -> paper.getTitle() != null)
				.map(paper -> new PaperRectangle(paper, paper.getX(), paper.getY(), width, height))
				.collect(Collectors.toList());

		paperRectangles.forEach(this::drawNode);
		this.colorNodes(this.root);
		this.drawEdges(this.root);
	}

	private void drawNode(PaperRectangle paper) {
		Coordinates coordinates = new Coordinates(paper.getX(), paper.getY());
		if(drawNodes.containsKey(coordinates)) return;

		if(papers.isEmpty()) root = paper;
		papers.add(paper);

		drawNodes.put(coordinates, paper);

		StackPane stack = new StackPane();
		String titleText = paper.getPaper().getTitle().substring(0, Math.min(paper.getPaper().getTitle().length(), 50));
		PaperText text = new PaperText(paper, titleText);
		stack.getChildren().addAll(paper, text);

		stack.setTranslateX(paper.getX());
		stack.setTranslateY(paper.getY());

		graphPane.getChildren().add(stack);

		paper.addEventHandler(MouseEvent.MOUSE_PRESSED, PaperRectangle::onMouseDownOnPaper);
		text.addEventHandler(MouseEvent.MOUSE_PRESSED, PaperText::onMouseDownOnText);
	}

	private void reset() {
		ObservableList<Node> children = graphPane.getChildren();

		for(int i = 0; i < children.size(); i = 0) {
			Node child = children.get(0);
			graphPane.getChildren().remove(child);
		}

		if(this.drawNodes != null) drawNodes.clear();
		this.papers.clear();
		this.root = null;
	}

	public void onMouseDown(MouseEvent mouseEvent) {
		System.out.println(mouseEvent.getX());
		System.out.println(mouseEvent.getY());
		switch(mouseEvent.getButton()) {
			case PRIMARY: {
				this.contextMenu.hide();
				break;
			}
			case SECONDARY: {
				this.graphPane.setOnContextMenuRequested(event ->
						this.contextMenu.show(this.graphPane, event.getScreenX(), event.getScreenY()));
				break;
			}
		}
		dragStartX = mouseEvent.getX();
		dragStartY = mouseEvent.getY();
	}

	private void onMouseUp(MouseEvent mouseEvent) {
		App.getScene().setCursor(Cursor.DEFAULT);
		cursorNormal = true;
	}

	public void onMouseDrag(MouseEvent mouseEvent) {
		if(!mouseEvent.isPrimaryButtonDown()) return;
		if(cursorNormal) App.getScene().setCursor(Cursor.CLOSED_HAND);

		doDrag(mouseEvent);
	}

	private synchronized void doDrag(MouseEvent mouseEvent) {
		double endX = mouseEvent.getX();
		double endY = mouseEvent.getY();

		double distanceX = -(endX - dragStartX);
		double distanceY = +(endY - dragStartY);

		if(hasMinimumMovementBeenFulfilled(distanceX, distanceY)) {
			double[] normalizedDistances = normalizeDragDistance(distanceX, distanceY);

			dragStartX = endX;
			dragStartY = endY;
			doDragOnRectangles(normalizedDistances[0], normalizedDistances[1]);
			doDragOnEdges(normalizedDistances[0], normalizedDistances[1]);
		}
	}

	private void doDragOnRectangles(double changeX, double changeY) {
		this.papers.forEach(paper -> {
			double newX = paper.getX() - changeX;
			double newY = paper.getY() + changeY;
			paper.getParent().setTranslateX(newX);
			paper.getParent().setTranslateY(newY);
			paper.setX(newX);
			paper.setY(newY);
		});

	}

	private void doDragOnEdges(double changeX, double changeY) {
		this.lineFromEdgeIdMap.values().forEach((Line line) -> {
			double newStartX = line.getStartX() - changeX;
			double newEndX = line.getEndX() - changeX;
			double newStartY = line.getStartY() + changeY;
			double newEndY = line.getEndY() + changeY;

			line.setStartX(newStartX);
			line.setEndX(newEndX);
			line.setStartY(newStartY);
			line.setEndY(newEndY);
		});
	}

	/**
	 * @param distanceX Actual X distance moved
	 * @param distanceY Actual Y distance moved
	 * @return Element 0 is how far to move the object in the X direction, Element 1 is how to far to move the object in the Y direction
	 */
	private double[] normalizeDragDistance(double distanceX, double distanceY) {
		double[] normalizedDistances = new double[2];

		double normalizationFactor = 1.0;
		double distanceXNormalized = distanceX / normalizationFactor;
		double distanceYNormalized = distanceY / normalizationFactor;

		normalizedDistances[0] = distanceXNormalized;
		normalizedDistances[1] = distanceYNormalized;
		return normalizedDistances;
	}

	private boolean hasMinimumMovementBeenFulfilled(double distanceX, double distanceY) {
		return  Math.abs(distanceX) > minDistanceForDrag ||
				Math.abs(distanceY) > minDistanceForDrag ||
				Math.abs(distanceX + distanceY) > minDistanceForDrag;
	}

	private void colorNodes(PaperRectangle root) {
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;

		ArrayList<PaperRectangle> papers = new ArrayList<>(this.papers);
		papers.add(root);

		for(PaperRectangle paper : papers) {
			min = Math.min(min, paper.getPaper().getReferences().size());
			max = Math.max(max, paper.getPaper().getReferences().size());
		}

		int[] colorCuttoffs = getColorCutoffs(min, max);

		papers.forEach(paper -> {
			Color color = calculateColor(paper.getPaper(), colorCuttoffs);
			PaperRectangle paperRectangle = this.getPaperRectangle(paper.getPaper());
			assert paperRectangle != null;
			PaperRectangle.setColor(paperRectangle, color);
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

	private PaperRectangle getPaperRectangle(Paper paper) {
		for(PaperRectangle paperRectangle : this.papers) {
			if(paperRectangle.getPaper().getDoi().equals(paper.getDoi())) {
				return paperRectangle;
			}
		}
		return null;
	}

	private void drawEdges(PaperRectangle root) {
		ArrayList<PaperRectangle> papers = this.papers.stream()
				.filter(paper -> !paper.equals(root))
				.collect(Collectors.toCollection(ArrayList::new));

		papers.forEach(paper -> drawEdge(root, paper));
	}

	private void drawEdge(PaperRectangle start, PaperRectangle end) {
		double[] startCoords;
		double[] endCoords;

		if(start.getPaper().compareTo(end.getPaper()) >= 0) {
			startCoords = start.getEdgeLeftCoords();
			endCoords = end.getEdgeRightCoords();
		} else {
			startCoords = start.getEdgeRightCoords();
			endCoords = end.getEdgeLeftCoords();
		}

		String edgeId = getEdgeId(start, end);

		if(this.hasEdge(edgeId)) {
			return;
		}

		ArrayList<PaperRectangle> edgeNodes = new ArrayList<>();
		edgeNodes.add(start);
		edgeNodes.add(end);

		this.nodesFromEdgeId.put(edgeId, edgeNodes);

		Line line = new Line();
		line.setStartX(startCoords[0]);
		line.setEndX(endCoords[0]);
		line.setStartY(startCoords[1]);
		line.setEndY(endCoords[1]);


		this.graphPane.getChildren().add(line);
		this.lineFromEdgeIdMap.put(edgeId, line);
	}

	private boolean hasEdge(String edgeId) {
		return this.lineFromEdgeIdMap.containsKey(edgeId);
	}

	private String getEdgeId(PaperRectangle start, PaperRectangle end) {
		return start.getPaper().getDoi() + end.getPaper().getDoi();
	}
}
