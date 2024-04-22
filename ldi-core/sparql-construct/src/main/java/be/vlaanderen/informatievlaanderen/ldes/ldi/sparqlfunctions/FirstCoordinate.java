package be.vlaanderen.informatievlaanderen.ldes.ldi.sparqlfunctions;

import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.apache.jena.geosparql.implementation.datatype.WKTDatatype;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase1;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.utils.SparqlFunctionsUtils.getNodeValue;

public class FirstCoordinate extends FunctionBase1 {

	public static final String NAME = "https://opengis.net/def/function/geosparql/custom#firstCoordinate";

	@Override
	public NodeValue exec(NodeValue lineString) {

		WKTDatatype wktDatatype = WKTDatatype.INSTANCE;
		GeometryWrapper wrapper = wktDatatype.read(lineString.asUnquotedString());

		return getFirstCoordinateOfLineString(wrapper);
	}

	private NodeValue getFirstCoordinateOfLineString(GeometryWrapper wrapper) {

		return getNodeValue(wrapper, wrapper.getXYGeometry().getCoordinates()[0]);
	}
}
