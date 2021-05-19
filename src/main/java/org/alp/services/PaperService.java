package org.alp.services;

import org.alp.models.Paper;
import org.alp.models.crossrefApi.getWorksResponse.GetWorksResponse;

import java.util.ArrayList;

public class PaperService {
	private static ArrayList<Paper> papers = null;


	public static ArrayList<Paper> getPapers() {
		return papers;
	}

	public static void setPapers(ArrayList<Paper> papers) {
		PaperService.papers = papers;
	}

	public static void clear() {
		papers = null;
	}
}
