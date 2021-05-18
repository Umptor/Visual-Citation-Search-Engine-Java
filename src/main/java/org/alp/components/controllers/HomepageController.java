package org.alp.components.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.alp.services.CssReader;

import java.net.http.HttpClient;

public class HomepageController {

	public HomepageController() {

	}

	private void showExampleGraph() {
				Graph graph = new SingleGraph("Tutorial 1");

		graph.addNode("A" );
		graph.addNode("B" );
		graph.addNode("C" );
		graph.addEdge("AB", "A", "B");
		graph.addEdge("BC", "B", "C");
		graph.addEdge("CA", "C", "A");

		System.setProperty("org.graphstream.ui", "javafx");


		graph.forEach((Node node) -> node.setAttribute("ui.label", "" + node.getId()));

		Node a = graph.getNode("A");
		a.setAttribute("ui.class", "marked");
		String stylesheet = CssReader.getInstance().getFile();
//		a

		graph.setAttribute("ui.stylesheet", stylesheet);
		graph.display();
	}

	@FXML
	public void initialize() {
		System.out.println("Initialized Homepage");
	}

	public void searchButtonOnClick(ActionEvent actionEvent) {
		var httpClient = HttpClient.newHttpClient();

//		httpClient.send()

	}
}
