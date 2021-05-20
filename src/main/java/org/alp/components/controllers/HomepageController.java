package org.alp.components.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.alp.App;
import org.alp.models.Paper;
import org.alp.models.crossrefApi.getWorksResponse.GetWorksResponse;
import org.alp.services.CrossRefService;
import org.alp.services.PaperService;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.alp.services.CssReader;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;


public class HomepageController {

	public TextField searchTextField;
	public Button homepageSearchButton;

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

	public void searchButtonOnClick(ActionEvent actionEvent) throws InterruptedException, IOException, URISyntaxException {
		ArrayList<Paper> papers = CrossRefService.getPaperByKeyWord(searchTextField.getText());
		PaperService.setPapers(papers);
		App.setRoot("searchResults");
	}
}
