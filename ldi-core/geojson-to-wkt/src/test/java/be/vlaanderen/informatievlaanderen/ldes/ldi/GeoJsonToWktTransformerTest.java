package be.vlaanderen.informatievlaanderen.ldes.ldi;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;

class GeoJsonToWktTransformerTest {

    private final GeoJsonToWktTransformer transformer = new GeoJsonToWktTransformer();

    @Test
    void shouldFooToBar() throws Exception {
        transformer.apply(
                RDFParser.source("geojson-point.json").lang(Lang.JSONLD).build().toModel()
        );



//        RDFNode coordinates = geojson.listStatements(null, createProperty("https://purl.org/geojson/vocab#coordinates"), (RDFNode) null)
//                .toList()
//                .stream()
//                .map(Statement::getObject)
//                .findFirst()
//                .orElseThrow();
//
//        List<Statement> coordinatesStatements = geojson.listStatements(coordinates.asResource(), null, (RDFNode) null).toList();
//
//        Model model = RDFParser.source("result.json").lang(Lang.JSONLD).build().toModel();


    }

}