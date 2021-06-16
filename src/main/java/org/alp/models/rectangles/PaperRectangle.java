package org.alp.models.rectangles;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import org.alp.models.Paper;
import org.alp.services.CrossRefService;
import org.alp.services.drawer.GraphDrawer;

public class PaperRectangle extends Rectangle {

	Paper paper;
	private final GraphDrawer graphDrawer;

	public PaperRectangle(Paper paper, GraphDrawer graphDrawer) {
		this.paper = paper;
		this.graphDrawer = graphDrawer;
	}

	public PaperRectangle(Paper paper, double width, double height, GraphDrawer graphDrawer) {
		super(width, height);
		this.paper = paper;
		this.graphDrawer = graphDrawer;
	}

	public PaperRectangle(Paper paper, double width, double height, Paint paint, GraphDrawer graphDrawer) {
		super(width, height, paint);
		this.paper = paper;
		this.graphDrawer = graphDrawer;
	}

	public PaperRectangle(Paper paper, double x, double y, double width, double height, GraphDrawer graphDrawer) {
		super(x, y, width, height);
		this.paper = paper;
		this.graphDrawer = graphDrawer;
	}

	public Paper getPaper() {
		return paper;
	}

	public void setPaper(Paper paper) {
		this.paper = paper;
	}

	public double getMiddleY() {
		return this.getPaper().getY() + (this.getHeight() / 2);
	}

	public double getRightSideX() {
		return this.getPaper().getX() + this.getWidth();
	}

	public double getLeftSideX() {
		return this.getPaper().getX();
	}

	/**
	 * @return Returns an array where the first element is x and the second is the y
	 */
	public double[] getEdgeLeftCoords() {
		double[] edgeCoords = new double[2];

		edgeCoords[0] = getLeftSideX();
		edgeCoords[1] = getMiddleY();
		return edgeCoords;
	}

	/**
	 * @return Returns an array where the first element is x and the second is the y
	 */
	public double[] getEdgeRightCoords() {
		double[] edgeCoords = new double[2];

		edgeCoords[0] = getRightSideX();
		edgeCoords[1] = getMiddleY();
		return edgeCoords;
	}

	public void onMouseDownOnPaper(MouseEvent mouseEvent) {
		PaperRectangle paperRectangle = (PaperRectangle) mouseEvent.getSource();

		onMouseDownOnPaper(mouseEvent.getButton());
	}

	protected void onMouseDownOnPaper(MouseButton mouseButton) {
		this.printInformationToConsole();
		if(mouseButton == MouseButton.PRIMARY) {
			this.changeRootNode();
		}
	}

	private void printInformationToConsole() {
		System.out.println("DOI: " + this.getPaper().getDoi());
		System.out.println("Paper: " + this.getPaper().getTitle());
		System.out.println("x: " + this.getX() + " y: " + this.getY());
		System.out.println("time: " + this.getPaper().getYear() + " " + this.getPaper().getMonth() + " " + this.getPaper().getDay());
		System.out.println("");
	}

	private void changeRootNode() {
		Paper root = graphDrawer.getRoot().getPaper();
		this.paper = CrossRefService.getFullReferences(this.paper, root);
		graphDrawer.drawGraph(this.getPaper());
	}

	public void setColor(Color color) {
		this.setFill(color);
	}
}
