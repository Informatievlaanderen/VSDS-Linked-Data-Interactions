package be.vlaanderen.informatievlaanderen.ldes.ldi.sparqlFunctions;

import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.apache.jena.geosparql.implementation.GeometryWrapperFactory;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.expr.NodeValue;
import org.locationtech.jts.geom.Coordinate;

public interface NodeHelper {

    default NodeValue getNodeValue(GeometryWrapper wrapper, Coordinate result) {

        Node midpoint = GeometryWrapperFactory
                .createPoint(result, wrapper.getGeometryDatatypeURI())
                .asNode();

        return NodeValue.makeNode(midpoint);
    }
}
