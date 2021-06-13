package org.alp.models.rectangles;

import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import org.alp.models.Paper;

public class PaperRectangle extends Rectangle {

	Paper paper;

	public PaperRectangle(Paper paper) {
		this.paper = paper;
	}

	public PaperRectangle(Paper paper, double width, double height) {
		super(width, height);
		this.paper = paper;
	}

	public PaperRectangle(Paper paper, double width, double height, Paint paint) {
		super(width, height, paint);
		this.paper = paper;
	}

	public PaperRectangle(Paper paper, double x, double y, double width, double height) {
		super(x, y, width, height);
		this.paper = paper;
	}

	public Paper getPaper() {
		return paper;
	}

	public void setPaper(Paper paper) {
		this.paper = paper;
	}

	public static void onMouseDownOnPaper(MouseEvent mouseEvent) {
		PaperRectangle paperRectangle = (PaperRectangle) mouseEvent.getSource();

		onMouseDownOnPaper(paperRectangle);
	}

	protected static void onMouseDownOnPaper(PaperRectangle paperRectangle) {
		System.out.println("x: " + paperRectangle.getX() + " y: " + paperRectangle.getY());
		System.out.println("time: " + paperRectangle.getPaper().getYear() + " " + paperRectangle.getPaper().getMonth() + " " + paperRectangle.getPaper().getDay());
		System.out.println(paperRectangle.getPaper().getTitle());
	}

	public static void setColor(PaperRectangle paperRectangle, Color color) {
		paperRectangle.setFill(color);
	}
}
