package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.WktConverter.COORDINATES;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.WktConverter.GEOMETRY;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeoJsonToWktTransformerTest {

    private final GeoJsonToWktTransformer transformer = new GeoJsonToWktTransformer();

    // TODO: 22/03/2023 param test
    @Test
    void testPoint() throws Exception {
        Model result = transformer.apply(
                RDFParser.source("geojson-point.json").lang(Lang.JSONLD).build().toModel()
        );

        List<String> resultWkts = result.listStatements(null, createProperty("http://www.w3.org/ns/locn#geometry"), (RDFNode) null)
                .mapWith(Statement::getObject)
                .mapWith(RDFNode::asLiteral)
                .mapWith(x -> x.getString() + "^^" + x.getDatatype().getURI())
                .toList();

        assertTrue(resultWkts.contains("POINT (100 0)^^http://www.opengis.net/ont/geosparql#wktLiteral"));
        assertTrue(resultWkts.contains("POINT (101 0)^^http://www.opengis.net/ont/geosparql#wktLiteral"));
        assertTrue(result.listStatements(null, GEOMETRY, (RDFNode) null).toList().isEmpty());
        assertTrue(result.listStatements(null, COORDINATES, (RDFNode) null).toList().isEmpty());
    }

    @Test
    void testPolygon() throws Exception {
        Model result = transformer.apply(
                RDFParser.source("geojson-polygon.json").lang(Lang.JSONLD).build().toModel()
        );

        List<String> resultWkts = result.listStatements(null, createProperty("http://www.w3.org/ns/locn#geometry"), (RDFNode) null)
                .mapWith(Statement::getObject)
                .mapWith(RDFNode::asLiteral)
                .mapWith(x -> x.getString() + "^^" + x.getDatatype().getURI())
                .toList();

        assertTrue(resultWkts.contains("POLYGON ((100 0, 101 0, 101 1, 100 1, 100 0), (100.8 0.8, 100.8 0.2, 100.2 0.2, 100.2 0.8, 100.8 0.8), (100.95 0.9, 100.95 0.5, 100.9 0.2, 100.9 0.5, 100.95 0.9))^^http://www.opengis.net/ont/geosparql#wktLiteral"));
        assertTrue(result.listStatements(null, GEOMETRY, (RDFNode) null).toList().isEmpty());
        assertTrue(result.listStatements(null, COORDINATES, (RDFNode) null).toList().isEmpty());
    }

}