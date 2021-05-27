package org.alp.services.graphstream;

import org.alp.models.Paper;
import org.alp.services.DateService;
import org.alp.services.PaperService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;

public class PaperCoordinateGiver {
	private static PaperCoordinateGiver paperCoordinateGiver;
	private static float numY = 1;

	private boolean calculated = false;

	private final float defaultX = 0;
	private final float defaultY = 0;
	private final float defaultZ = 0;
	private final float differenceBetweenNodesY = -1f;


	private PaperCoordinateGiver() {}

	public static PaperCoordinateGiver initialize() {
		if(paperCoordinateGiver == null) {
			paperCoordinateGiver = new PaperCoordinateGiver();
		}
		return paperCoordinateGiver;
	}

	public void determineCoordinates(Paper root) {
		if(calculated) return;

		this.determineCoordinatesAlgo(root);
		numY = 0.5f;
	}

	private void determineCoordinatesAlgo(Paper root) {
		if(root.getReferences() == null || root.getReferences().size() == 0) {
			root.setX(defaultX);
			root.setY(defaultY);
			root.setZ(defaultZ);
			return;
		}

		ArrayList<Paper> rootArr = new ArrayList<>();
		rootArr.add(root);

		float smallestYear = (float) Math.min(determineX(rootArr), root.getPublishedPrint().getYear());
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
		// Smallest year is always root if there are no nodes to the left is invalid bc crossref D:
		final int[] smallestYear = {LocalDate.now().getYear()};
		if(papers == null) return smallestYear[0];

		papers.forEach(paper -> {

			if(paper.getTitle() == null) {
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

		determineYAlgo(root, defaultY);
	}

	private float determineYAlgo(Paper root, float height) {
		if(root == null || root.getTitle() == null) return height;

		ArrayList<Paper> sortedReferences = PaperService.sortReferencesByDecreasingYear(root);

		float heightNew = height;
		for(Paper paper : sortedReferences) {
			heightNew = determineYAlgo(paper, heightNew);
		}

		root.setY(height);
		return heightNew + differenceBetweenNodesY;
	}

	private void determineZ(Paper root) {
		if(root == null) return;

		ArrayList<Paper> papers = PaperService.flattenPapers(root);
		papers.forEach(paper -> paper.setZ(defaultZ));
	}
}
