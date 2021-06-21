package org.alp.components.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.alp.App;
import org.alp.models.Paper;
import org.alp.models.PaperTableElement;
import org.alp.services.PaperService;
import org.alp.services.SavePaperService;

import java.io.IOException;
import java.util.ArrayList;


public class SearchResultsController {

	@FXML
	public Button showButton;
	@FXML
	public TableView<PaperTableElement> resultsTable;
	@FXML
	public TableView<PaperTableElement> savedPapersTable;

	PaperTableElement selectedPaper;
	ArrayList<Paper> papers;

	ArrayList<Paper> savedPapers = new ArrayList<>();
	private boolean selectedSavedNode = false;

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

		SavePaperService.setSearchResultsController(this);
		System.out.println("Initialized Search Results Page");
	}

	private void initializeTable() {
		this.setTableColumns();

		this.resultsTable.setItems(FXCollections.observableArrayList());
		this.setTableRowOnClick(resultsTable, false);
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

	public void onShowButtonMouseClick() {
		System.out.println("clicked show button\nGood Luck, this might take a while");
		if(this.selectedPaper == null) {
			System.out.println("No Paper selected");
			return;
		}
		Paper selected = null;
		if(this.selectedPaper.getDoi() == null) {
			System.out.println("Paper doesn't have DOI, what");
			return;
		}
		ArrayList<Paper> papersLoop = selectedSavedNode ? savedPapers : papers;
		for(Paper paper : papersLoop) {
			if(paper.getDoi().equals(selectedPaper.getDoi())) {
				selected = paper;
				break;
			}
		}
		assert selected != null;

		GraphPageController.paper = PaperService.getFullReferences(selected, null);
		openGraphWindow();
	}

	private void openGraphWindow() {
		Parent root;
		try {
			root = FXMLLoader.load(App.class.getResource("fxml/graphpage.fxml"));
			Scene scene = new Scene(root, 800, 600);
			GraphPageController.scene = scene;
			GraphPageController.draw(GraphPageController.graphPaneStatic, GraphPageController.overlayPaneStatic);
			GraphPageController.searchResultsController = this;
			Stage stage = new Stage();

			stage.setTitle("Citation Graph");
			stage.setScene(scene);
			stage.show();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Dont call this method, use the method inside SavePaperService instead
	 */
	public boolean savePaper(Paper paper) {
		if(this.savedPapersTable.getItems().isEmpty()) {
			this.initialSavedPapersTable();
		}
		PaperTableElement tableElement = new PaperTableElement(
				paper.getTitle(),
				paper.getAuthors().length > 0 ? paper.getAuthors()[0].getFullname() : "",
				paper.getDoi(),
				paper.getReferences().size());
		if(this.savedPapersTable.getItems().contains(tableElement)) {
			return false;
		}

		this.savedPapersTable.getItems().add(tableElement);
		this.savedPapers.add(paper);
		return true;
	}

	private void initialSavedPapersTable() {
		this.savedPapersTable.setVisible(true);
		this.setSavedPapersTableColumns();
		this.setTableRowOnClick(savedPapersTable, true);
	}

	private void setSavedPapersTableColumns() {
		TableColumn<PaperTableElement, String> titleColumn = new TableColumn<>("Title");
		titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
		titleColumn.setResizable(true);

		this.savedPapersTable.getColumns().add(titleColumn);
	}

	private void setTableRowOnClick(TableView<PaperTableElement> table, boolean savedNode) {
		table.setRowFactory(tv -> {
			TableRow<PaperTableElement> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (!row.isEmpty()) {
					this.selectedPaper = row.getItem();
					this.selectedSavedNode = savedNode;
				}
			});
			return row;
		});
	}
}
