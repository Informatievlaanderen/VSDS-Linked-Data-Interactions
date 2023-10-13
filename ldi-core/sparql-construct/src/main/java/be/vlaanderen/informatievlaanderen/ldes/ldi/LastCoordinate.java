package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.apache.jena.geosparql.implementation.GeometryWrapperFactory;
import org.apache.jena.geosparql.implementation.datatype.WKTDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.expr.NodeValue;
import org.locationtech.jts.geom.Coordinate;

public class LastCoordinate extends FunctionBase2WithName {
    public static final String name = "https://w3id.org/tree#lastCoordinate";

    @Override
    public NodeValue exec(NodeValue wktLiteral, NodeValue index)  {
        WKTDatatype wktDatatype = WKTDatatype.INSTANCE;
        GeometryWrapper wrapper = wktDatatype.read(wktLiteral.asUnquotedString());

        return getLastCoordinateOfLineString(wrapper);
    }

    private NodeValue getLastCoordinateOfLineString(GeometryWrapper wrapper) {

        Coordinate[] coordinates = wrapper.getXYGeometry().getCoordinates();
        Node lastCoordinate = GeometryWrapperFactory.createPoint(coordinates[coordinates.length - 1], wrapper.getGeometryDatatypeURI())
                .asNode();

        return NodeValue.makeNode(lastCoordinate);
    }

    @Override
    String getName() {
        return name;
    }
}
