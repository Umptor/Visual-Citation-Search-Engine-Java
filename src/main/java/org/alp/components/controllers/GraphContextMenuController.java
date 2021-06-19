package org.alp.components.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import org.alp.models.rectangles.PaperRectangle;
import org.alp.services.SavePaperService;

import java.util.function.Consumer;

public class GraphContextMenuController {

	private PaperRectangle paperRectangle;
	private final ContextMenu contextMenu;
	private final Pane graphPane;

	public GraphContextMenuController(Pane graphPane) {
		this.contextMenu = new ContextMenu();
		this.graphPane = graphPane;
	}

	public void setFocusMenuItem(MenuItem menuItem) {
		menuItem.setOnAction(event -> this.focusAction());
		this.contextMenu.getItems().add(menuItem);
	}

	private void focusAction() {
		this.paperRectangle.setRootNode();
	}

	public void setRememberMenuItem(MenuItem menuItem) {
		menuItem.setOnAction(event -> this.rememberAction());
		this.contextMenu.getItems().add(menuItem);
	}

	private void rememberAction() {
		SavePaperService.save(this.paperRectangle.getPaper());
	}

	public void setMenuItem(MenuItem menuItem, Consumer<ActionEvent> onClick) {
		menuItem.setOnAction(onClick::accept);
		this.contextMenu.getItems().add(menuItem);
	}

	public ContextMenu getContextMenu() {
		return this.contextMenu;
	}

	public void hide() {
		this.contextMenu.hide();
	}

	public void show(PaperRectangle paperRectangle) {
		if(paperRectangle == null) {
			return;
		}
		this.paperRectangle = paperRectangle;

		this.graphPane.setOnContextMenuRequested(event ->
				this.contextMenu.show(this.graphPane, event.getScreenX(), event.getScreenY()));
	}
}
