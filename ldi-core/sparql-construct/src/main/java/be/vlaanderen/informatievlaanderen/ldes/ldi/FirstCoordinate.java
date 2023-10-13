package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.apache.jena.geosparql.implementation.GeometryWrapperFactory;
import org.apache.jena.geosparql.implementation.datatype.WKTDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.expr.NodeValue;

public class FirstCoordinate extends FunctionBase2WithName {
	public final String name = "https://w3id.org/tree#firstCoordinate";

	@Override
	public NodeValue exec(NodeValue wktLiteral, NodeValue index) {
		WKTDatatype wktDatatype = WKTDatatype.INSTANCE;
		GeometryWrapper wrapper = wktDatatype.read(wktLiteral.asUnquotedString());

		return getFirstCoordinateOfLineString(wrapper);
	}


	private NodeValue getFirstCoordinateOfLineString(GeometryWrapper wrapper) {
		Node firstCoordinate = GeometryWrapperFactory.createPoint(wrapper.getXYGeometry().getCoordinates()[0], wrapper.getGeometryDatatypeURI())
				.asNode();

		return NodeValue.makeNode(firstCoordinate);
	}

	@Override
	String getName() {
		return name;
	}
}
