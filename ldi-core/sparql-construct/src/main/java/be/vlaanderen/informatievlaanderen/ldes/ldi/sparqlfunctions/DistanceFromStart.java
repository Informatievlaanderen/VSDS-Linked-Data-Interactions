package be.vlaanderen.informatievlaanderen.ldes.ldi.sparqlfunctions;

import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.apache.jena.geosparql.implementation.datatype.WKTDatatype;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.utils.SparqlFunctionsUtils.*;

public class DistanceFromStart extends FunctionBase2 {

	public static final String NAME = "https://opengis.net/def/function/geosparql/custom#distanceFromStart";

	@Override
	public NodeValue exec(NodeValue wktLiteral, NodeValue nodeValue1) {

		WKTDatatype wktDatatype = WKTDatatype.INSTANCE;
		GeometryWrapper wrapper = wktDatatype.read(wktLiteral.asUnquotedString());

		return getDistanceFromStart(wrapper);
	}

	private NodeValue getDistanceFromStart(GeometryWrapper wrapper) {

		Coordinate[] coordinates = wrapper.getXYGeometry().getCoordinates();
		Coordinate thePoint = new MidPoint().getMidPointCoordinate(getTotalLineLength(coordinates) / 2, 0, coordinates);

		if (coordinates.length == 2) {
			return NodeValue.makeDouble(calculateDistance(coordinates[0], thePoint));
		}

		int segmentNumber = getSegmentNumberForCoordinate(coordinates, thePoint);
		Coordinate[] coordinatesFromStartToPoint = getCoordinatesFromStartToPoint(coordinates, segmentNumber, thePoint);

		return NodeValue.makeDouble(getTotalLineLength(coordinatesFromStartToPoint));
	}

	private static Coordinate[] getCoordinatesFromStartToPoint(Coordinate[] coordinates, int segmentNumber,
			Coordinate thePoint) {

		List<Coordinate> coordinateList = Arrays.stream(coordinates)
				.limit(segmentNumber + 1L)
				.collect(Collectors.toList());

		coordinateList.add(thePoint);

		return coordinateList.toArray(new Coordinate[0]);
	}

	int getSegmentNumberForCoordinate(Coordinate[] coords, Coordinate thePoint) {

		List<LineSegment> lines = createLineSegmentsList(coords);

		return IntStream.range(0, lines.size())
				.filter(i -> pointIsOnLine(lines.get(i), thePoint))
				.findFirst()
				.orElseThrow();
	}

	private static List<LineSegment> createLineSegmentsList(Coordinate[] coords) {

		return IntStream.range(0, coords.length - 1)
				.mapToObj(i -> new LineSegment(coords[i], coords[i + 1]))
				.toList();
	}
}