package org.alp.models.rectangles;

public class Coordinates {
	double x;
	double y;

	public Coordinates(double x, double y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		Coordinates that = (Coordinates) o;
		return Double.compare(that.x, x) == 0 && Double.compare(that.y, y) == 0;
	}
}
