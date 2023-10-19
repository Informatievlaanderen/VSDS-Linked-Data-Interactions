package be.vlaanderen.informatievlaanderen.ldes.ldi.sparqlFunctions;

import be.vlaanderen.informatievlaanderen.ldes.ldi.formulaUtils.CoordinateFinder;
import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.apache.jena.geosparql.implementation.GeometryWrapperFactory;
import org.apache.jena.geosparql.implementation.datatype.WKTDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;
import org.locationtech.jts.geom.Coordinate;

import java.util.Arrays;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.formulaUtils.DistanceCalculator.getLineLengths;

public class MidPoint extends FunctionBase2 {

	public static final String name = "https://w3id.org/tree#midPoint";

	@Override
	public NodeValue exec(NodeValue wktLiteral, NodeValue index) {

		WKTDatatype wktDatatype = WKTDatatype.INSTANCE;
		GeometryWrapper wrapper = wktDatatype.read(wktLiteral.asUnquotedString());

		return getMidPoint(wrapper);
	}

	private NodeValue getMidPoint(GeometryWrapper wrapper) {

		Coordinate[] coordinates = wrapper.getXYGeometry().getCoordinates();
		double[] lineLengths = getLineLengths(coordinates);
		double halfOfLineLength = getHalfOfLineLengths(lineLengths);
		int nthLength = getNthLength(lineLengths, halfOfLineLength);

		Coordinate result = getMidPointCoordinate(halfOfLineLength, nthLength, coordinates);

		Node midpoint = GeometryWrapperFactory.createPoint(result, wrapper.getGeometryDatatypeURI())
				.asNode();

		return NodeValue.makeNode(midpoint);
	}

	public static Coordinate getMidPointCoordinate(double halfOfLineLength, int nthLength, Coordinate[] coords) {

		double distanceToFirst = halfOfLineLength - getSumOfLengthsBeforeSegment(getLineLengths(coords), nthLength);

		return CoordinateFinder.findOnSegmentByDistance(coords[nthLength], coords[nthLength + 1], distanceToFirst);
	}

	private static double getSumOfLengthsBeforeSegment(double[] lineLengths, int nthLength) {

		return Arrays.stream(lineLengths)
				.limit(nthLength)
				.sum();
	}

	public static int getNthLength(double[] lineLengths, double halfOfLineLength) {

		List<Double> lengths = DoubleStream.of(lineLengths).boxed().toList();

		return IntStream.range(0, lengths.size())
				.filter(WhenSumOfLengthsIsBiggerThanHalf(halfOfLineLength, lengths))
				.findFirst()
				.getAsInt();
	}

	private static IntPredicate WhenSumOfLengthsIsBiggerThanHalf(double halfOfLineLength, List<Double> lengths) {

		return i -> lengths.subList(0, i + 1).stream()
				.mapToDouble(Double::doubleValue)
				.sum() > halfOfLineLength;
	}

	private static double getHalfOfLineLengths(double[] lineLengths) {

		return Arrays.stream(lineLengths)
				.sum() / 2;
	}
}