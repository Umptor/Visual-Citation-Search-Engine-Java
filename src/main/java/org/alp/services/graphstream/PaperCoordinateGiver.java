package org.alp.services.graphstream;

import org.alp.models.Paper;
import org.alp.models.crossrefApi.Reference;
import org.alp.services.DateService;
import org.alp.services.PaperService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;

public class PaperCoordinateGiver {
	private static PaperCoordinateGiver paperCoordinateGiver;
	private static float numY = 1;

	private boolean calculated = false;

	private PaperCoordinateGiver() {}

	public static PaperCoordinateGiver initialize() {
		if(paperCoordinateGiver != null) {
			return paperCoordinateGiver;
		}

		return new PaperCoordinateGiver();
	}

	public void determineCoordinates(Paper root) {
		if(calculated) return;

		this.determineCoordinatesAlgo(root);
//		root.setX(0);
//		root.setY(0);
		numY = 1f;
	}

	private void determineCoordinatesAlgo(Paper root) {
		if(root.getReferences() == null || root.getReferences().size() == 0) {
			root.setX(0f);
			root.setY(0f);
			root.setZ(0f);
			return;
		}

		ArrayList<Paper> rootArr = new ArrayList<>();
		rootArr.add(root);

		float smallestYear = (float) Math.min(determineX(rootArr), root.getPublishedPrint().getYear());
//		float smallestYear = (float) root.getPublishedPrint().getYear();
//		determineX(root.getReferences());
		determineY(root);
		determineZ(root);

		// Normalize X
		Stack<Paper> papers = new Stack<>();
		papers.push(root);

		while(!papers.isEmpty()) {
			var current = papers.pop();
			if(current.getTitle() == null) continue;
			current.setX(current.getX() - smallestYear);

			if(current.getReferences() != null)
				current.getReferences().stream()
						.filter((Paper paper) -> Objects.nonNull(paper) && paper.getTitle() != null).collect(Collectors.toList())
						.forEach(papers::push);
		}
	}

	private Integer determineX(ArrayList<Paper> papers) {
		// Smallest year is always root if there are no nodes to the left
		final int[] smallestYear = {LocalDate.now().getYear()};
		if(papers == null) return smallestYear[0];

		papers.forEach(paper -> {

			if(paper.getTitle() == null || paper.getX() != null) {
				return;
			}

			LocalDate publishingDate = LocalDate.of(
					paper.getYear(),
					paper.getMonth(),
					paper.getDay());

			smallestYear[0] = Math.min(smallestYear[0], publishingDate.getYear());

			float daysInYear = (float) DateService.daysInYear(publishingDate);
			float daysSinceBeginningOfYear = (float) DateService.daysSinceBeginningOfYear(publishingDate);

			float distance = (float) (publishingDate.getYear()) + (daysSinceBeginningOfYear / daysInYear);
			paper.setX(distance);

			determineX(paper.getReferences());
			smallestYear[0] = Math.min(smallestYear[0], determineX(paper.getReferences()));
		});
		return smallestYear[0];
	}

	private void determineY(Paper root) {
		if(root == null || root.getTitle() == null) return;

//		ArrayList<Paper> papers = PaperService.flattenPapers(root);

		ArrayList<Paper> sortedReferences = PaperService.sortReferencesByDecreasingYear(root);



//		papers.stream().filter(paper -> paper.getTitle() != null &&
//				(paper.getPublishedPrint() != null || paper.getPublishedOnline() != null) && paper.getY() == null)
//				.forEach((Paper paper) -> {
//			paper.setY(((float) numY));
//			numY -= 0.5f;
//		});
	}

	private float differenceBetweenNodes = -0.5f;

	private float determineYAlgo(Paper root, float height) {
		if(root == null || root.getTitle() == null) return height +  differenceBetweenNodes;




	}

	private void determineZ(Paper root) {
		if(root == null) return;

		ArrayList<Paper> papers = PaperService.flattenPapers(root);
		papers.forEach(paper -> paper.setZ(0f));
	}
}
