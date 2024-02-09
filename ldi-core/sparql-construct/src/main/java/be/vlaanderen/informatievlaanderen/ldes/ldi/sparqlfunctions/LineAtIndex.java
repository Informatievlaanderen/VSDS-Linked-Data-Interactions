package be.vlaanderen.informatievlaanderen.ldes.ldi.sparqlfunctions;

import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.apache.jena.geosparql.implementation.GeometryWrapperFactory;
import org.apache.jena.geosparql.implementation.datatype.WKTDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LineAtIndex extends FunctionBase2 {

	private static final Logger LOGGER = LoggerFactory.getLogger(LineAtIndex.class);
	public static final String NAME = "https://opengis.net/def/function/geosparql/custom#lineAtIndex";

	@Override
	public NodeValue exec(NodeValue lineString, NodeValue number) {

		WKTDatatype wktDatatype = WKTDatatype.INSTANCE;
		GeometryWrapper wrapper = wktDatatype.read(lineString.asUnquotedString());

		MultiLineString multiLineString = getMultiLineString(lineString);

		int i = number.getDecimal().intValue();

		return getLineAtIndex(wrapper, multiLineString, i);
	}

	private NodeValue getLineAtIndex(GeometryWrapper wrapper, MultiLineString multiLineString, int i) {

		checkIndexInRange(multiLineString, i);

		Coordinate[] coordinates = multiLineString.getGeometryN(i).getCoordinates();

		Node wkt = GeometryWrapperFactory.createLineString(coordinates, wrapper.getGeometryDatatypeURI())
				.asNode();

		return NodeValue.makeNode(wkt);
	}

	private static void checkIndexInRange(MultiLineString multiLineString, int i) {

		boolean inRange = 0 <= i && i < multiLineString.getNumGeometries();

		if (!inRange)
			throw new IllegalArgumentException("Index is not in range !");
	}

	private static MultiLineString getMultiLineString(NodeValue wktLiteral) {

		WKTReader wktReader = new WKTReader();
		MultiLineString multiLineString = null;

		try {
			multiLineString = (MultiLineString) wktReader.read(wktLiteral.asUnquotedString());
		} catch (ParseException e) {
			LOGGER.error("MultiLineString parsing went wrong", e);
		}

		return multiLineString;
	}
}
