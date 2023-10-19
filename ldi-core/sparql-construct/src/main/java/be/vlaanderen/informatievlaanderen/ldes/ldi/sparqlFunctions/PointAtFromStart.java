package be.vlaanderen.informatievlaanderen.ldes.ldi.sparqlFunctions;

import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.apache.jena.geosparql.implementation.GeometryWrapperFactory;
import org.apache.jena.geosparql.implementation.datatype.WKTDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase3;
import org.locationtech.jts.geom.Coordinate;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.formulaUtils.DistanceCalculator.getLineLengths;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.sparqlFunctions.MidPoint.getNthLength;

public class PointAtFromStart extends FunctionBase3 {

	public static final String name = "https://w3id.org/tree#pointAtFromStart";

	@Override
	public NodeValue exec(NodeValue wktLiteral, NodeValue nodeValue1, NodeValue offset) {

		WKTDatatype wktDatatype = WKTDatatype.INSTANCE;
		GeometryWrapper wrapper = wktDatatype.read(wktLiteral.asUnquotedString());
		double os = Double.parseDouble(offset.asString());

		return getPointAtFromStart(wrapper, os);
	}

	private NodeValue getPointAtFromStart(GeometryWrapper wrapper, double os) {

		Coordinate[] coords = wrapper.getXYGeometry().getCoordinates();
		double[] lineLengths = getLineLengths(coords);

		Coordinate result = MidPoint.getMidPointCoordinate(os, getNthLength(lineLengths, os), coords);

		Node midpoint = GeometryWrapperFactory.createPoint(result, wrapper.getGeometryDatatypeURI())
				.asNode();

		return NodeValue.makeNode(midpoint);
	}
}