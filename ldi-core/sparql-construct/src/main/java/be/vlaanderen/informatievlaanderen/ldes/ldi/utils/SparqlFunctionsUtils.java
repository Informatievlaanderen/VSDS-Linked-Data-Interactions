package be.vlaanderen.informatievlaanderen.ldes.ldi.utils;

import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.apache.jena.geosparql.implementation.GeometryWrapperFactory;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.expr.NodeValue;
import org.locationtech.jts.geom.Coordinate;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class SparqlFunctionsUtils {

	static double SEMI_MAJOR_AXIS_MT = 6378137;
	static double SEMI_MINOR_AXIS_MT = 6356752.314245;
	static double FLATTENING = 1 / 298.257223563;
	static double ERROR_TOLERANCE = 1e-12;

	private SparqlFunctionsUtils() {
	}

	/**
	 * Vincenty's Formula
	 * returns a double representing the distance in meters
	 */
	public static double calculateDistance(double latitude1, double longitude1, double latitude2, double longitude2) {

		double U1 = Math.atan((1 - FLATTENING) * Math.tan(Math.toRadians(latitude1)));
		double U2 = Math.atan((1 - FLATTENING) * Math.tan(Math.toRadians(latitude2)));

		double sinU1 = Math.sin(U1);
		double cosU1 = Math.cos(U1);
		double sinU2 = Math.sin(U2);
		double cosU2 = Math.cos(U2);

		double longitudeDifference = Math.toRadians(longitude2 - longitude1);
		double previousLongitudeDifference;

		double sinSigma, cosSigma, sigma, sinAlpha, cosSqAlpha, cos2SigmaM;

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
			double C = FLATTENING / 16 * cosSqAlpha * (4 + FLATTENING * (4 - 3 * cosSqAlpha));
			longitudeDifference = Math.toRadians(longitude2 - longitude1) + (1 - C) * FLATTENING * sinAlpha *
					(sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * Math.pow(cos2SigmaM, 2))));
		} while (Math.abs(longitudeDifference - previousLongitudeDifference) > ERROR_TOLERANCE);

		double uSq = cosSqAlpha * (Math.pow(SEMI_MAJOR_AXIS_MT, 2) - Math.pow(SEMI_MINOR_AXIS_MT, 2))
				/ Math.pow(SEMI_MINOR_AXIS_MT, 2);

		double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
		double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));

		double deltaSigma = B * sinSigma * (cos2SigmaM + B / 4 * (cosSigma * (-1 + 2 * Math.pow(cos2SigmaM, 2))
				- B / 6 * cos2SigmaM * (-3 + 4 * Math.pow(sinSigma, 2)) * (-3 + 4 * Math.pow(cos2SigmaM, 2))));

		return SEMI_MINOR_AXIS_MT * A * (sigma - deltaSigma);
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
				.mapToDouble(i -> calculateDistance(coords[i].y, coords[i].x, coords[i + 1].y, coords[i + 1].x));
	}

	public static Coordinate findOnSegmentByDistance(Coordinate c1, Coordinate c2, double distanceToX) {

		double length = calculateDistance(c1.y, c1.x, c2.y, c2.x);
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
}
