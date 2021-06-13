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
	public static double height = 80.0;
	public static double width = 150.0;

	private double startX = 0.0;
	private double startY = 0.0;
	private double minDistanceForDrag = 2.0;
	private boolean cursorNormal = true;
	private boolean shouldDrag = true;

	private ArrayList<PaperRectangle> papers = new ArrayList<>();
	private PaperRectangle root;



	private ContextMenu contextMenu;
	private Scene scene;

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

	private void setupCamera(double startX, double startY) {
		Camera camera = new PerspectiveCamera(true);
		camera.translateXProperty().setValue(0);
		camera.translateYProperty().setValue(0);
		camera.translateZProperty().setValue(-50);
		camera.setNearClip(0);
		camera.setNearClip(10000);

		this.scene.setCamera(camera);

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
	}

	private void drawNode(PaperRectangle paper) {
		Coordinates coordinates = new Coordinates(paper.getX(), paper.getY());
		if(drawNodes.containsKey(coordinates)) return;

		if(papers.isEmpty()) root = paper;
		papers.add(paper);

//		PaperRectangle.setColor((paper, Color.LIGHTGRAY));

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
		if(this.papers != null) this.papers.clear();
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
		if(!shouldDrag) return;
		startX = mouseEvent.getX();
		startY = mouseEvent.getY();
	}

	private void onMouseUp(MouseEvent mouseEvent) {
		App.getScene().setCursor(Cursor.DEFAULT);
		cursorNormal = true;
	}

	public void onMouseDrag(MouseEvent mouseEvent) {
		if(!mouseEvent.isPrimaryButtonDown() || !shouldDrag) return;
		if(cursorNormal) App.getScene().setCursor(Cursor.CLOSED_HAND);

		doDrag(mouseEvent);
	}

	private synchronized void doDrag(MouseEvent mouseEvent) {
		double endX = mouseEvent.getX();
		double endY = mouseEvent.getY();

		double distanceX = -(endX - startX);
		double distanceY = +(endY - startY);


		double normalizationFactor = 1.0;
		double distanceXNormalized = distanceX / normalizationFactor;
		double distanceYNormalized = distanceY / normalizationFactor;

		if(Math.abs(distanceX) > minDistanceForDrag ||
				Math.abs(distanceY) > minDistanceForDrag ||
				Math.abs(distanceX + distanceY) > minDistanceForDrag) {
			// Do drag
			startX = endX;
			startY = endY;
			this.papers.forEach(paper -> {
				double newX = paper.getX() - distanceXNormalized;
				double newY = paper.getY() + distanceYNormalized;
				paper.getParent().setTranslateX(newX);
				paper.getParent().setTranslateY(newY);
				paper.setX(newX);
				paper.setY(newY);
			});
		}
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
}
