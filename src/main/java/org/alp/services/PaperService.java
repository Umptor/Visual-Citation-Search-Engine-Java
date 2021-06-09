package org.alp.services;

import org.alp.models.Paper;

import java.util.*;
import java.util.stream.Collectors;

public class PaperService {
	private static ArrayList<Paper> papers = null;


	public static ArrayList<Paper> getPapers() {
		return papers;
	}

	public static void setPapers(ArrayList<Paper> papers) {
		PaperService.papers = papers;
	}

	public static ArrayList<Paper> sortReferencesByIncreasingYear(Paper root) {
		if(root == null || root.getReferences() == null) return new ArrayList<>();
		return root.getReferences().stream()
				.filter(paper -> paper.getPublishedOnline() != null || paper.getPublishedPrint() != null)
				.sorted(Paper::compareTo)
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public static ArrayList<Paper> sortReferencesByDecreasingYear(Paper root) {
		var a = sortReferencesByIncreasingYear(root);
		Collections.reverse(a);
		return a;
	}

	public static ArrayList<Paper> sortPapersByIncreasingYear(ArrayList<Paper> papers) {
		return papers.stream().filter(paper -> paper.getPublishedOnline() != null || paper.getPublishedPrint() != null)
				.sorted(Paper::compareTo)
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public static ArrayList<Paper> sortPapersByDecreasingYear(ArrayList<Paper> papers) {
		var a = sortPapersByIncreasingYear(papers);
		Collections.reverse(a);
		return a;
	}
}
