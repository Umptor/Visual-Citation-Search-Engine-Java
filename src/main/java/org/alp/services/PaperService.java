package org.alp.services;

import org.alp.models.Paper;
import org.alp.models.crossrefApi.getWorksResponse.GetWorksResponse;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

	public static ArrayList<Paper> flattenPapers(Paper root) {
		var papers = flattenPapersAlgo(root);

		return papers.stream().distinct().collect(Collectors.toCollection(ArrayList::new));
	}

	private static ArrayList<Paper> flattenPapersAlgo(Paper root) {
		ArrayList<Paper> papers = new ArrayList<>();
		if(root == null) {
			return papers;
		}

		papers.add(root);

		if(root.getReferences() == null) {
			return papers;
		}

		root.getReferences().forEach(paper -> {
			var newPapers = flattenPapers(paper);
			papers.addAll(newPapers);
		});

		return papers;
	}

	public static ArrayList<Paper> sortReferencesByDecreasingYear(Paper root) {
		if(root == null || root.getReferences() == null) return new ArrayList<>();
		return root.getReferences().stream()
				.filter(paper -> paper.getPublishedOnline() != null || paper.getPublishedPrint() != null)
				.sorted(Paper::compareTo)
				.sorted(Collections.reverseOrder())
				.collect(Collectors.toCollection(ArrayList::new));
	}
}
