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

	public HomepageController() { }

	@FXML
	public void initialize() {
		System.out.println("Initialized Homepage");
	}

	public void searchButtonOnClick() throws InterruptedException, IOException, URISyntaxException {
		ArrayList<Paper> papers = CrossRefService.getPaperByKeyWord(searchTextField.getText());
		PaperService.setPapers(papers);
		App.setRoot("searchResults");
	}
}
