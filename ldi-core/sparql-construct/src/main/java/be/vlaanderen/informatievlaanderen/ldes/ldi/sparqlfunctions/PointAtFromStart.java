package be.vlaanderen.informatievlaanderen.ldes.ldi.sparqlfunctions;

import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.apache.jena.geosparql.implementation.datatype.WKTDatatype;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;
import org.locationtech.jts.geom.Coordinate;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.utils.SparqlFunctionsUtils.getLineLengths;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.utils.SparqlFunctionsUtils.getNodeValue;

public class PointAtFromStart extends FunctionBase2 {

	public static final String NAME = "https://opengis.net/def/function/geosparql/custom#pointAtFromStart";

	@Override
	public NodeValue exec(NodeValue lineString, NodeValue offset) {

		WKTDatatype wktDatatype = WKTDatatype.INSTANCE;
		GeometryWrapper wrapper = wktDatatype.read(lineString.asUnquotedString());
		double os = Double.parseDouble(offset.asString());

		return getPointAtFromStart(wrapper, os);
	}

	private NodeValue getPointAtFromStart(GeometryWrapper wrapper, double os) {

		Coordinate[] coords = wrapper.getXYGeometry().getCoordinates();
		double[] lineLengths = getLineLengths(coords);
		MidPoint mp = new MidPoint();

		Coordinate result = mp.getMidPointCoordinate(os, mp.getNthLength(lineLengths, os), coords);

		return getNodeValue(wrapper, result);
	}
}