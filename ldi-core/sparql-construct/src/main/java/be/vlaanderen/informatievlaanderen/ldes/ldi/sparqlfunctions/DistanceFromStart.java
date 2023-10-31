package be.vlaanderen.informatievlaanderen.ldes.ldi.sparqlfunctions;

import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.apache.jena.geosparql.implementation.datatype.WKTDatatype;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.utils.SparqlFunctionsUtils.*;

public class DistanceFromStart extends FunctionBase2 {

	public static final String NAME = "https://opengis.net/def/function/geosparql/custom#distanceFromStart";

	@Override
	public NodeValue exec(NodeValue wktLiteral, NodeValue point) {

		WKTDatatype wktDatatype = WKTDatatype.INSTANCE;
		GeometryWrapper wktWrapper = wktDatatype.read(wktLiteral.asUnquotedString());
		GeometryWrapper pointWrapper = wktDatatype.read(point.asUnquotedString());

		return getDistanceFromStart(wktWrapper, pointWrapper);
	}

	private NodeValue getDistanceFromStart(GeometryWrapper wktWrapper, GeometryWrapper pointWrapper) {

		Coordinate[] coordinates = wktWrapper.getXYGeometry().getCoordinates();
		Coordinate thePoint = pointWrapper.getXYGeometry().getCoordinate();

		if (coordinates.length == 2) {
			return NodeValue.makeDouble(calculateDistance(coordinates[0], thePoint));
		}

		int segmentNumber = getSegmentNumberForCoordinate(coordinates, thePoint);
		Coordinate[] coordinatesFromStartToPoint = getCoordinatesFromStartToPoint(coordinates, segmentNumber, thePoint);

		return NodeValue.makeDouble(getTotalLineLength(coordinatesFromStartToPoint));
	}

	private Coordinate[] getCoordinatesFromStartToPoint(Coordinate[] coordinates, int segmentNumber,
			Coordinate thePoint) {

		List<Coordinate> coordinateList = Arrays.stream(coordinates)
				.limit(segmentNumber + 1L)
				.collect(Collectors.toList());

		coordinateList.add(thePoint);

		return coordinateList.toArray(new Coordinate[0]);
	}

	private int getSegmentNumberForCoordinate(Coordinate[] coords, Coordinate thePoint) {

		LineSegment[] lineSegs = createLineSegmentsArray(coords);

		return IntStream.range(0, lineSegs.length)
				.mapToObj(x -> new IndexAndDistance(x, distanceFromLine(lineSegs[x], thePoint)))
				.min(Comparator.comparing(x -> x.distance))
				.map(x -> x.index)
				.orElseThrow();
	}

	private LineSegment[] createLineSegmentsArray(Coordinate[] coords) {

		return IntStream.range(0, coords.length - 1)
				.mapToObj(i -> new LineSegment(coords[i], coords[i + 1]))
				.toArray(LineSegment[]::new);
	}

	private record IndexAndDistance(int index, double distance) {
	}
}