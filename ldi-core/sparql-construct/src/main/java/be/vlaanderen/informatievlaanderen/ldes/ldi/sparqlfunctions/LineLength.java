package be.vlaanderen.informatievlaanderen.ldes.ldi.sparqlfunctions;

import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.apache.jena.geosparql.implementation.datatype.WKTDatatype;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;
import org.locationtech.jts.geom.Coordinate;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.utils.SparqlFunctionsUtils.getTotalLineLength;

public class LineLength extends FunctionBase2 {

	public static final String NAME = "https://w3id.org/tree#lineLength";

	@Override
	public NodeValue exec(NodeValue wktLiteral, NodeValue nodeValue1) {

		WKTDatatype wktDatatype = WKTDatatype.INSTANCE;
		GeometryWrapper wrapper = wktDatatype.read(wktLiteral.asUnquotedString());

		return getLineLengthOfString(wrapper);
	}

	private NodeValue getLineLengthOfString(GeometryWrapper wrapper) {

		Coordinate[] coordinates = wrapper.getXYGeometry().getCoordinates();
		double lineLength = (double) Math.round((getTotalLineLength(coordinates)) * 100) / 100;

		return NodeValue.makeDouble(lineLength);
	}
}