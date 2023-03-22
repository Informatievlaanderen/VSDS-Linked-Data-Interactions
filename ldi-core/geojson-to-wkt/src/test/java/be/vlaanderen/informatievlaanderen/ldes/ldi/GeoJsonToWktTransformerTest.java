package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeoJsonToWktTransformerTest {

    private final GeoJsonToWktTransformer transformer = new GeoJsonToWktTransformer();

    @Test
    void shouldFooToBar() throws Exception {
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
    }

}