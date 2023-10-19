package be.vlaanderen.informatievlaanderen.ldes.ldi.formulaUtils;

import org.locationtech.jts.geom.Coordinate;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.formulaUtils.DistanceCalculator.calculateDistance;

public class CoordinateFinder {

	public static Coordinate findOnSegmentByDistance(Coordinate c1, Coordinate c2, double distanceToX) {

		double length = calculateDistance(c1.y, c1.x, c2.y, c2.x);
		double ratio = distanceToX / length;
		double x = ratio * c2.x + (1.0 - ratio) * c1.x;
		double y = ratio * c2.y + (1.0 - ratio) * c1.y;

		return new Coordinate(x, y);
	}
}
