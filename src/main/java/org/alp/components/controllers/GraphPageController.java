package org.alp.components.controllers;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import org.alp.models.Paper;
import org.alp.services.drawer.GraphDrawer;

public class GraphPageController {

	public static Paper paper;
	public static Scene scene;
	public static Pane graphPaneStatic;

	@FXML
	public Pane graphPane;

	@FXML
	public void initialize() {
		System.out.println("New Page Created");
		graphPaneStatic = this.graphPane;
	}

	public static void draw(Pane graphPane) {
		GraphDrawer graphDrawer = new GraphDrawer(graphPane, scene);
		graphDrawer.drawGraph(paper);
	}
}
