package be.vlaanderen.informatievlaanderen.ldes.ldi.sparqlFunctions;

import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.apache.jena.geosparql.implementation.datatype.WKTDatatype;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;
import org.locationtech.jts.geom.Coordinate;

import java.util.List;
import java.util.function.IntPredicate;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.utils.SparqlFunctionsUtils.*;
import static java.util.Arrays.stream;

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

		double halfOfLineLength = stream(lineLengths).sum() / 2;

		int nthLength = getNthLength(lineLengths, halfOfLineLength);


		Coordinate result = getMidPointCoordinate(halfOfLineLength, nthLength, coordinates);

		return getNodeValue(wrapper, result);
	}

	Coordinate getMidPointCoordinate(double offset, int nthLength, Coordinate[] coords) {

		double distanceToFirst = offset - getSumOfLengthsBeforeSegment(getLineLengths(coords), nthLength);

		return findOnSegmentByDistance(coords[nthLength], coords[nthLength + 1], distanceToFirst);
	}

	private double getSumOfLengthsBeforeSegment(double[] lineLengths, int nthLength) {

		return stream(lineLengths)
				.limit(nthLength)
				.sum();
	}

	int getNthLength(double[] lineLengths, double offset) {

		List<Double> lengths = DoubleStream.of(lineLengths).boxed().toList();

		return IntStream.range(0, lengths.size())
				.filter(whenSumOfLengthsIsBiggerThanOffset(offset, lengths))
				.findFirst()
				.orElseThrow();
	}

	private static IntPredicate whenSumOfLengthsIsBiggerThanOffset(double offset, List<Double> lengths) {

		return i -> lengths.subList(0, i + 1).stream()
				.mapToDouble(Double::doubleValue)
				.sum() > offset;
	}
}