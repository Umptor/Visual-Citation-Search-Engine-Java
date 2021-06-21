package org.alp.services;

import org.alp.components.controllers.SearchResultsController;
import org.alp.models.Paper;

import java.util.ArrayList;

public class SavePaperService {

	private static final ArrayList<Paper> savedPapers = new ArrayList<>();
	private static SearchResultsController searchResultsController;

	public static void setSearchResultsController(SearchResultsController searchResultsController) {
		SavePaperService.searchResultsController = searchResultsController;
	}

	public static void save(Paper paper) {
		if(savedPapers.contains(paper)) return;
		if(searchResultsController == null) return;

		boolean saved = searchResultsController.savePaper(paper);
		if(saved) savedPapers.add(paper);
		else System.out.println("Failed to save paper with doi: " + paper.getDoi());
	}

}
