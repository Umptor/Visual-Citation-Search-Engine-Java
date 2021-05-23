package org.alp.services.graphstream;

import org.alp.models.Paper;

import java.util.ArrayList;

public class PaperCoordinateGiver {
	private static PaperCoordinateGiver paperCoordinateGiver;
	private static int num = 1;

	private PaperCoordinateGiver() {}

	public static PaperCoordinateGiver initialize() {
		if(paperCoordinateGiver != null) {
			return paperCoordinateGiver;
		}

		return new PaperCoordinateGiver();
	}

	public void determineCoordinates(Paper root) {
		root.setX(num * 5);
		root.setX(num++ * 5);
		this.determineCoordinatesAlgo(root.getReferences());
		num = 0;
	}

	public void determineCoordinatesAlgo(ArrayList<Paper> references) {
		if(references == null || references.size() == 0) {
			return;
		}
//
//		Paper paper = references.get(0);
//		paper.setX(num * 5);
//		paper.setY(num++ * 5);
//
//		determineCoordinatesAlgo(paper.getReferences());
		references.forEach((Paper paper) -> {
			paper.setX(num * 5);
			paper.setY(num++ * 5);
		});
		references.forEach((Paper paper) -> determineCoordinatesAlgo(paper.getReferences()));
	}
}
