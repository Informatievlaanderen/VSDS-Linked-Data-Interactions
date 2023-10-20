package be.vlaanderen.informatievlaanderen.ldes.ldi.sparqlFunctions;

import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.apache.jena.geosparql.implementation.datatype.WKTDatatype;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;

public class FirstCoordinate extends FunctionBase2 implements NodeHelper {

	public static final String name = "https://w3id.org/tree#firstCoordinate";

	@Override
	public NodeValue exec(NodeValue wktLiteral, NodeValue index) {

		WKTDatatype wktDatatype = WKTDatatype.INSTANCE;
		GeometryWrapper wrapper = wktDatatype.read(wktLiteral.asUnquotedString());

		return getFirstCoordinateOfLineString(wrapper);
	}

	private NodeValue getFirstCoordinateOfLineString(GeometryWrapper wrapper) {

		return getNodeValue(wrapper, wrapper.getXYGeometry().getCoordinates()[0]);
	}
}
