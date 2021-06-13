package org.alp.models.rectangles;

import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public class PaperText extends Text {
	PaperRectangle paperRectangle;

	public PaperText(PaperRectangle paperRectangle, String text) {
		super(text);
		this.paperRectangle = paperRectangle;
	}

	public PaperText(PaperRectangle paperRectangle, double x, double y, String text) {
		super(x, y, text);
		this.paperRectangle = paperRectangle;
	}

	public PaperRectangle getPaperRectangle() {
		return paperRectangle;
	}

	public void setPaperRectangle(PaperRectangle paperRectangle) {
		this.paperRectangle = paperRectangle;
	}

	public static void onMouseDownOnText(MouseEvent mouseEvent) {
		PaperText paperText = (PaperText) mouseEvent.getSource();
		PaperRectangle.onMouseDownOnPaper(paperText.getPaperRectangle());
	}
}
