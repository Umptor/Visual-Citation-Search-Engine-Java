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

		float smallestYear = (float) Math.min(determineX(rootArr, new ArrayList<>()), root.getPublishedPrint().getYear());
		determineY(root);
		determineZ(root);

		// Normalize X

	}

	private Paper root;

	private Integer determineX(ArrayList<Paper> papers, ArrayList<Paper> visited) {

	}

	private void determineY(Paper root) {

	}

	private void determineYAlgo(Paper root, float height) {

	}

	private void determineZ(Paper root) {
		if(root == null) return;

		ArrayList<Paper> papers = PaperService.flattenPapers(root);
		papers.forEach(paper -> paper.setZ(defaultZ));
	}
}
