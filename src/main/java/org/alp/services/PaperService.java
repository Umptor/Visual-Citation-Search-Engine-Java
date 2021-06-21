package org.alp.services;

import org.alp.models.Paper;
import org.alp.models.crossrefApi.AuthorCrossRef;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PaperService {
	private static ArrayList<Paper> papers = null;
	private static final HashMap<String, Paper> foundFullPapers = new HashMap<>();


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
		return papers.stream().filter(paper -> paper.getYear() != null)
				.sorted(Paper::compareTo)
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public static ArrayList<Paper> sortPapersByDecreasingYear(ArrayList<Paper> papers) {
		return papers.stream().filter(paper -> paper.getYear() != null)
				.sorted((paper1, paper12) -> -paper1.compareTo(paper12))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public static Paper findPaper(Paper rootPaper, String doiToFind) {
		if(rootPaper.getDoi().equals(doiToFind)) return rootPaper;
		else return rootPaper.getReferences().get(rootPaper.getReferences().indexOf(new Paper(doiToFind)));
	}

	public static Paper getFullReferences(Paper root, Paper oldRoot) {
		if(foundFullPapers.get(root.getDoi()) != null) return foundFullPapers.get(root.getDoi());

		CountDownLatch latch = new CountDownLatch(2);
		Paper[] crossRefPaperArr = {null};
		new Thread(() -> {
			crossRefPaperArr[0] = CrossRefService.getFullReferences(root, oldRoot);
			System.out.println("Returned from CrossRefService");
			latch.countDown();
		}).start();
		Paper[] openCitationPaperArr = {null};
		new Thread(() -> {
			openCitationPaperArr[0] = OpenCitationService.getFullReferences(root, oldRoot);
			System.out.println("Returned from OpenCitationService");
			latch.countDown();
		}).start();

		try {
			latch.await();
		} catch(InterruptedException e) {
			System.out.println("I think the program didn't wait for oc and rf to return papers?");
		}

		Paper crossRefPaper = crossRefPaperArr[0];
		Paper openCitationPaper = openCitationPaperArr[0];

		if(crossRefPaper == null && openCitationPaper == null) {
			System.out.println("Couldn't change root node, Problem with the API");
			return oldRoot;
		}

		Paper newRoot = combinePapers(crossRefPaper, openCitationPaper);

		foundFullPapers.put(newRoot.getDoi(), newRoot);
		return newRoot;
	}

	private static Paper combinePapers(Paper crossRef, Paper openCitation) {
		Paper paper = new Paper();

		ArrayList<AuthorCrossRef> authors = new ArrayList<>();
		ArrayList<Paper> references = new ArrayList<>();

		if(crossRef != null) {
			if(openCitation == null) {
				paper.setDoi(crossRef.getDoi());
				paper.setTitle(crossRef.getTitle());
				paper.setYear(crossRef.getYear());
				paper.setMonth(crossRef.getMonth());
				paper.setDay(crossRef.getDay());
				paper.setPaperAbstract(crossRef.getPaperAbstract());
			}
			authors.addAll(Arrays.asList(crossRef.getAuthors()));
			references.addAll(crossRef.getReferences());
		}

		if(openCitation != null) {
			paper.setDoi(openCitation.getDoi());
			paper.setTitle(openCitation.getTitle());
			paper.setYear(openCitation.getYear());
			paper.setMonth(openCitation.getMonth());
			paper.setDay(openCitation.getDay());
			paper.setPaperAbstract("");
			authors.addAll(Arrays.asList(openCitation.getAuthors()));
			references.addAll(openCitation.getReferences());
		}

		paper.setAuthors(authors.toArray(AuthorCrossRef[]::new));

		references = references.stream().filter(distinctByKey(Paper::getDoi)).collect(Collectors.toCollection(ArrayList::new));
		paper.setReferences(references);

		return paper;
	}

	private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Set<Object> seen = ConcurrentHashMap.newKeySet();
		return t -> seen.add(keyExtractor.apply(t));
	}

	static void addFullReferences(Paper root, List<Paper> references, List<Paper> alreadyFoundPapers) {
		references.forEach(paper -> {
			if(paper.getDoi() == null || paper.getTitle() == null) return;
			boolean isFullPaper = false;
			if(root.getReferences().contains(paper)) {
				int index = root.getReferences().indexOf(paper);
				Paper reference = root.getReferences().get(index);
				if(reference.getDoi() != null && reference.getTitle() != null) isFullPaper = true;

				if(!isFullPaper) {
					root.getReferences().set(index, paper);
				}
			} else {
				root.getReferences().add(paper);
			}
		});

		root.getReferences().addAll(alreadyFoundPapers);
	}

	static void addFullPapersToFoundList(Paper root) {
		foundFullPapers.putIfAbsent(root.getDoi(), root);
	}

}
