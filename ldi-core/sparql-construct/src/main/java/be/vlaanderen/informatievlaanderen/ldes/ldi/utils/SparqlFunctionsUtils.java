package be.vlaanderen.informatievlaanderen.ldes.ldi.utils;

import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.apache.jena.geosparql.implementation.GeometryWrapperFactory;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.expr.NodeValue;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;

import java.util.Arrays;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class SparqlFunctionsUtils {

	static final double SEMI_MAJOR_AXIS_MT = 6378137;
	static final double SEMI_MINOR_AXIS_MT = 6356752.314245;
	static final double FLATTENING = 1 / 298.257223563;
	static final double ERROR_TOLERANCE = 1e-12;

	private SparqlFunctionsUtils() {
	}

	/**
	 * Vincenty's Formula
	 * returns a double representing the distance in meters
	 */
	public static double calculateDistance(Coordinate coordinate1, Coordinate coordinate2) {

		double latitude1 = coordinate1.y;
		double longitude1 = coordinate1.x;
		double latitude2 = coordinate2.y;
		double longitude2 = coordinate2.x;

		double u1 = Math.atan((1 - FLATTENING) * Math.tan(Math.toRadians(latitude1)));
		double u2 = Math.atan((1 - FLATTENING) * Math.tan(Math.toRadians(latitude2)));

		double sinU1 = Math.sin(u1);
		double cosU1 = Math.cos(u1);
		double sinU2 = Math.sin(u2);
		double cosU2 = Math.cos(u2);

		double longitudeDifference = Math.toRadians(longitude2 - longitude1);
		double previousLongitudeDifference;

		double sinSigma;
		double cosSigma;
		double sigma;
		double sinAlpha;
		double cosSqAlpha;
		double cos2SigmaM;

		do {
			sinSigma = Math.sqrt(Math.pow(cosU2 * Math.sin(longitudeDifference), 2) +
					Math.pow(cosU1 * sinU2 - sinU1 * cosU2 * Math.cos(longitudeDifference), 2));
			cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * Math.cos(longitudeDifference);
			sigma = Math.atan2(sinSigma, cosSigma);
			sinAlpha = cosU1 * cosU2 * Math.sin(longitudeDifference) / sinSigma;
			cosSqAlpha = 1 - Math.pow(sinAlpha, 2);
			cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;
			if (Double.isNaN(cos2SigmaM)) {
				cos2SigmaM = 0;
			}
			previousLongitudeDifference = longitudeDifference;
			double c = FLATTENING / 16 * cosSqAlpha * (4 + FLATTENING * (4 - 3 * cosSqAlpha));
			longitudeDifference = Math.toRadians(longitude2 - longitude1) + (1 - c) * FLATTENING * sinAlpha *
					(sigma + c * sinSigma * (cos2SigmaM + c * cosSigma * (-1 + 2 * Math.pow(cos2SigmaM, 2))));
		} while (Math.abs(longitudeDifference - previousLongitudeDifference) > ERROR_TOLERANCE);

		double uSq = cosSqAlpha * (Math.pow(SEMI_MAJOR_AXIS_MT, 2) - Math.pow(SEMI_MINOR_AXIS_MT, 2))
				/ Math.pow(SEMI_MINOR_AXIS_MT, 2);

		double a = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
		double b = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));

		double deltaSigma = b * sinSigma * (cos2SigmaM + b / 4 * (cosSigma * (-1 + 2 * Math.pow(cos2SigmaM, 2))
				- b / 6 * cos2SigmaM * (-3 + 4 * Math.pow(sinSigma, 2)) * (-3 + 4 * Math.pow(cos2SigmaM, 2))));

		return SEMI_MINOR_AXIS_MT * a * (sigma - deltaSigma);
	}

	public static double[] getLineLengths(Coordinate[] coords) {

		return getLineLengthsStream(coords)
				.toArray();
	}

	public static double getTotalLineLength(Coordinate[] coords) {

		return getLineLengthsStream(coords)
				.sum();
	}

	private static DoubleStream getLineLengthsStream(Coordinate[] coords) {

		return IntStream.range(0, coords.length - 1)
				.mapToDouble(i -> calculateDistance(coords[i], coords[i + 1]));
	}

	public static Coordinate findOnSegmentByDistance(Coordinate c1, Coordinate c2, double distanceToX) {

		double length = calculateDistance(c1, c2);
		double ratio = distanceToX / length;
		double x = ratio * c2.x + (1.0 - ratio) * c1.x;
		double y = ratio * c2.y + (1.0 - ratio) * c1.y;

		return new Coordinate(x, y);
	}

	public static NodeValue getNodeValue(GeometryWrapper wrapper, Coordinate result) {

		Node midpoint = GeometryWrapperFactory
				.createPoint(result, wrapper.getGeometryDatatypeURI())
				.asNode();

		return NodeValue.makeNode(midpoint);
	}

	public static Coordinate getCoordinatesFromPointAsString(String midpoint) {

		double[] coords = Arrays.stream(midpoint.replace("POINT(", "")
				.replace(")", "")
				.split(" "))
				.mapToDouble(Double::parseDouble)
				.toArray();

		return new Coordinate(coords[0], coords[1]);
	}

	/**
	 * @see <a href="https://en.wikipedia.org/wiki/Distance_from_a_point_to_a_line">distance from a point to a line</a>
	 */
	public static double distanceFromLine(LineSegment lineSegment, Coordinate p0) {

		Coordinate p1 = lineSegment.p0;
		Coordinate p2 = lineSegment.p1;

		return Math.abs((p2.x - p1.x) * (p1.y - p0.y) - (p1.x - p0.x) * (p2.y - p1.y))
				/ Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
	}
}