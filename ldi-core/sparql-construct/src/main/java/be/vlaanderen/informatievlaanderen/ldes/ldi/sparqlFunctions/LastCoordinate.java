package be.vlaanderen.informatievlaanderen.ldes.ldi.sparqlFunctions;

import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.apache.jena.geosparql.implementation.datatype.WKTDatatype;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;
import org.locationtech.jts.geom.Coordinate;

public class LastCoordinate extends FunctionBase2 implements NodeHelper{

	public static final String name = "https://w3id.org/tree#lastCoordinate";

	@Override
	public NodeValue exec(NodeValue wktLiteral, NodeValue index) {

		WKTDatatype wktDatatype = WKTDatatype.INSTANCE;
		GeometryWrapper wrapper = wktDatatype.read(wktLiteral.asUnquotedString());

		return getLastCoordinateOfLineString(wrapper);
	}

	private NodeValue getLastCoordinateOfLineString(GeometryWrapper wrapper) {

		Coordinate[] coords = wrapper.getXYGeometry().getCoordinates();

		return getNodeValue(wrapper, coords[coords.length - 1]);
	}
}
