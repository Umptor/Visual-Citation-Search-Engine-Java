package org.alp.services.graphstream;

import org.alp.models.Paper;
import org.alp.services.DateService;
import org.alp.services.PaperService;

import java.util.ArrayList;

public class PaperCoordinateGiver {
	private static PaperCoordinateGiver paperCoordinateGiver;

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
		this.determineCoordinatesAlgo(root);
	}

	private void determineCoordinatesAlgo(Paper root) {
		if(root.getReferences() == null || root.getReferences().size() == 0) {
			root.setX(defaultX);
			root.setY(defaultY);
			root.setZ(defaultZ);
			return;
		}


		determineX(root);
		determineY(root);
		determineZ(root);
	}

	private void determineX(Paper root) {
		ArrayList<Paper> papers = new ArrayList<>();
		papers.add(root);
		papers.addAll(root.getReferences());

		papers.forEach(paper -> {
			int daysInYear = DateService.daysInYear(paper.getDate());
			int daysSinceBeginningOfYear = DateService.daysSinceBeginningOfYear(paper.getDate());

			paper.setX((float) paper.getYear() + (float) daysSinceBeginningOfYear/(float) daysInYear);
		});

		normalizeX(root, papers);
	}

	private void normalizeX(Paper root, ArrayList<Paper> papers) {
		papers.forEach(reference -> reference.setX(reference.getX() - root.getYear()));
	}

	private void determineY(Paper root) {
		root.setY(defaultY);

		ArrayList<Paper> left = new ArrayList<>();
		ArrayList<Paper> right = new ArrayList<>();
		float heightLeft = defaultY + differenceBetweenNodesY;
		float heightRight = defaultY + differenceBetweenNodesY;

		for(Paper paper : root.getReferences()) {
			if(paper.compareTo(root) <= 0) {
				left.add(paper);
			}
			else {
				right.add(paper);
			}
		}

		left = PaperService.sortPapersByIncreasingYear(left);
		determineYAlgo(left, heightLeft);

		right = PaperService.sortPapersByDecreasingYear(right);
		determineYAlgo(right, heightRight);
	}

	private void determineYAlgo(ArrayList<Paper> references, float height) {
		int counter = 0;
		for(Paper paper : references) {
			if(counter++ % 2 == 0) {
				paper.setY(height);
				height += differenceBetweenNodesY;
			}
			else {
				paper.setY(height * -1);
			}
		}
	}

	private void determineZ(Paper root) {
		root.setZ(defaultZ);

		root.getReferences().forEach(paper -> paper.setZ(defaultZ));
	}
}
