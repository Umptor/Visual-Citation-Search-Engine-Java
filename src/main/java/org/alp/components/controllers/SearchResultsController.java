package org.alp.components.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.alp.models.Paper;
import org.alp.models.PaperTableElement;
import org.alp.services.CrossRefService;
import org.alp.services.GraphStreamService;
import org.alp.services.PaperService;

import java.net.URISyntaxException;
import java.util.ArrayList;


public class SearchResultsController {

	@FXML
	public Button showButton;
	@FXML
	public TableView<PaperTableElement> resultsTable;

	PaperTableElement selectedPaper;
	ArrayList<Paper> papers;

	public SearchResultsController() {
		System.out.println("controller for searchResults");
		papers = PaperService.getPapers();
	}

	@FXML
	public void initialize() {
		this.initializeTable();

		papers.forEach(paper -> {
			String title = "no title";
			String author = "no author";

			if(paper.getTitle() != null) {
				title = paper.getTitle();
			}
			if(paper.getAuthors() != null) {
				author = paper.getAuthors()[0].getFullname();
			}
			var a = new PaperTableElement(title, author, paper.getDoi(), paper.getReferences() == null ? 0 : paper.getReferences().size());
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

		TableColumn<PaperTableElement, Integer> referenceCountColumn = new TableColumn<>("Reference Count");
		referenceCountColumn.setCellValueFactory(new PropertyValueFactory<>("referenceCount"));
		referenceCountColumn.setMaxWidth(200.0);

		resultsTable.getColumns().addAll(titleColumn, authorColumn, referenceCountColumn);
	}

	public void onShowButtonMouseClick(MouseEvent mouseEvent) throws URISyntaxException, InterruptedException {
		System.out.println("clicked show button\nGood Luck, this might take a while");
		if(this.selectedPaper == null) {
			System.out.println("No Paper selected");
			return;
		}
		var graph = new GraphStreamService();
		Paper selected = null;
		for(Paper paper : papers) {
			if(this.selectedPaper.getDoi() == null) {
				System.out.println("Paper doesn't have DOI, what");
				return;
			}
			if(paper.getDoi().equals(selectedPaper.getDoi())) {
				selected = paper;
				break;
			}
		}
		assert selected != null;

		CrossRefService.getRelatedPapers(selected, 2);

		graph.addNode(selected, selected.getReferences());

		graph.showGraph();
	}
}
