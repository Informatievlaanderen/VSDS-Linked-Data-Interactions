package be.vlaanderen.informatievlaanderen.ldes.ldi.sparqlfunctions;

import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.apache.jena.geosparql.implementation.datatype.WKTDatatype;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;
import org.locationtech.jts.geom.Coordinate;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.utils.SparqlFunctionsUtils.getNodeValue;

public class LastCoordinate extends FunctionBase2 {

	public static final String NAME = "https://w3id.org/tree#lastCoordinate";

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
