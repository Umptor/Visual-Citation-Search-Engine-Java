package org.alp.components.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.alp.models.crossrefApi.getWorksResponse.Item;
import org.alp.services.SearchResultsService;

import java.util.Arrays;


public class SearchResultsController {

	@FXML
	public Button showButton;
	@FXML
	public TableView<PaperTableElement> resultsTable;

	Item[] papers;

	public SearchResultsController() {
		System.out.println("controller for searchResults");
		papers = SearchResultsService.getGetWorksResponse().getMessage().getItems();

	}

	@FXML
	public void initialize() {
		this.initializeTable();

		Arrays.asList(papers).forEach(paper -> {
			String title = "no title";
			String author = "no author";

			if(paper.getTitle() != null) {
				title = paper.getTitle()[0];
			}
			if(paper.getAuthors() != null) {
				author = paper.getAuthors()[0].getFullname();
			}
			var a = new PaperTableElement(title, author);
			resultsTable.getItems().add(a);
		});

		System.out.println("Initialized Search Results Page");
	}

	private void initializeTable() {
		this.setTableColumns();

		this.resultsTable.setItems(FXCollections.observableArrayList());
	}

	private void setTableColumns() {
		TableColumn<PaperTableElement, String> titleColumn = new TableColumn<>("Title");
		titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
		titleColumn.setMaxWidth(600.0);

		TableColumn<PaperTableElement, String> authorColumn = new TableColumn<>("Author");
		authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
		authorColumn.setMaxWidth(200.0);

		resultsTable.getColumns().addAll(titleColumn, authorColumn);
	}

	public void onShowButtonMouseClick(MouseEvent mouseEvent) {
		System.out.println("clicked show button");
	}
}
