package org.alp.components.controllers;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import org.alp.models.Paper;
import org.alp.services.drawer.GraphDrawer;

public class GraphPageController {

	public static Paper paper;
	public static Scene scene;
	public static Pane graphPaneStatic;
	public static Pane overlayPaneStatic;

	public static SearchResultsController searchResultsController;

	@FXML
	public Pane graphPane;
	@FXML
	public Pane overlayPane;

	@FXML
	public void initialize() {
		System.out.println("New Page Created");
		graphPaneStatic = this.graphPane;
		overlayPaneStatic = this.overlayPane;
	}

	public static void draw(Pane graphPane, Pane overlayPane) {
		GraphDrawer graphDrawer = new GraphDrawer(graphPane, overlayPane, scene);
		graphDrawer.drawGraph(paper);
	}
}
