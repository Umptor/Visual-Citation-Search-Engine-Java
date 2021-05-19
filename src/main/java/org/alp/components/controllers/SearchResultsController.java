package org.alp.components.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.alp.models.Paper;
import org.alp.models.crossrefApi.getWorksResponse.Item;
import org.alp.models.crossrefApi.getWorksResponse.Reference;
import org.alp.services.GraphStreamService;
import org.alp.services.SearchResultsService;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;


public class SearchResultsController {

	@FXML
	public Button showButton;
	@FXML
	public TableView<PaperTableElement> resultsTable;

	PaperTableElement selectedPaper;
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
			var a = new PaperTableElement(title, author, paper.getDoi());
			resultsTable.getItems().add(a);
		});

		System.out.println("Initialized Search Results Page");
	}

	private void initializeTable() {
		this.setTableColumns();

		this.resultsTable.setItems(FXCollections.observableArrayList());
		resultsTable.setRowFactory(tv -> {
			TableRow<PaperTableElement> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (!row.isEmpty()) {

					this.selectedPaper = row.getItem();
				}
			});
			return row;
		});
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
		if(this.selectedPaper == null) {
			System.out.println("No Paper selected");
			return;
		}
		var graph = new GraphStreamService();
		Item selected = null;
		for(Item paper : papers) {
			if(this.selectedPaper.getDoi() == null) {
				System.out.println("Paper doesn't have DOI, what");
				return;
			}
			if(paper.getDoi().equals(selectedPaper.getDoi())) {
				selected = paper;
				break;
			}
		}

		ArrayList<String> references = new ArrayList<>();
		String[] referencesStringArr = new String[]{};
		assert selected != null;
		if(selected.getReferences() != null) {
			Arrays.stream(selected.getReferences()).map(Reference::getDoi).filter(Objects::nonNull).forEach(references::add);
			referencesStringArr = references.toArray(referencesStringArr);
		}

		graph.addNode(selected.getDoi(), references.isEmpty() ? new String[]{} : referencesStringArr);

		graph.showGraph();
	}
}
