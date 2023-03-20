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
import org.geotools.feature.FeatureCollection;
import org.geotools.geojson.GeoJSON;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.WKTWriter;

import org.geotools.geojson.geom.GeometryJSON;
import org.locationtech.jts.io.WKTWriter;
import org.opengis.feature.Feature;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.junit.jupiter.api.Assertions.*;

class GeoJsonToWktTransformerTest {

    @Test
    void shouldFooToBar() throws Exception {
        Model geojson = RDFParser.source("geojson.json").lang(Lang.JSONLD).build().toModel();

        RDFNode geometry = geojson.listStatements(null, createProperty("https://purl.org/geojson/vocab#geometry"), (RDFNode) null)
                .toList()
                .stream()
                .map(Statement::getObject)
                .findFirst()
                .orElseThrow();

        List<Statement> geometryStatements = geojson.listStatements(geometry.asResource(), null, (RDFNode) null).toList();

        RDFNode coordinates = geojson.listStatements(null, createProperty("https://purl.org/geojson/vocab#coordinates"), (RDFNode) null)
                .toList()
                .stream()
                .map(Statement::getObject)
                .findFirst()
                .orElseThrow();

        List<Statement> coordinatesStatements = geojson.listStatements(coordinates.asResource(), null, (RDFNode) null).toList();

        Model model = RDFParser.source("result.json").lang(Lang.JSONLD).build().toModel();


    }

    @Test
    void foo() throws Exception {

        Model geojson = RDFParser.source("geojson.json").lang(Lang.JSONLD).build().toModel();
        String geoJsonString = RDFWriter.source(geojson).lang(Lang.JSONLD).build().asString();


        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(geoJsonString);
        JsonNode coordinatesNode =
//                rootNode
//                .at("/https://purl.org/geojson/vocab#geometry/https://purl.org/geojson/vocab#coordinates");

        rootNode.at("/@graph/2/geojson:coordinates");


        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate[] coordinates = new Coordinate[coordinatesNode.size()];
        for (int i = 0; i < coordinatesNode.size(); i++) {
            JsonNode coordinateNode = coordinatesNode.get(i).get("@list");
            double x = coordinateNode.get(0).asDouble();
            double y = coordinateNode.get(1).asDouble();
            coordinates[i] = new Coordinate(x, y);
        }
        Polygon polygon = geometryFactory.createPolygon(coordinates);

        String wkt = polygon.toText();
        System.out.println(wkt);
    }

    private List<Statement> getFirsts(Model geojson) {
        RDFNode first = geojson.listStatements(null, createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#first"), (RDFNode) null)
                .toList()
                .stream()
                .map(Statement::getObject)
                .findFirst()
                .orElseThrow();

        return geojson.listStatements(first.asResource(), null, (RDFNode) null).toList();
    }

    private List<Statement> getRests(Model geojson) {
        RDFNode rest = geojson.listStatements(null, createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#rest"), (RDFNode) null)
                .toList()
                .stream()
                .map(Statement::getObject)
                .findFirst()
                .orElseThrow();

        return geojson.listStatements(rest.asResource(), null, (RDFNode) null).toList();
    }

    @Test
    void bar() {
        // First, you need to load the GeoSPARQL vocabulary
//        GeoSPARQL.registerAll();

        Model model = RDFParser.source("geojson.json").lang(Lang.JSONLD).build().toModel();

// Then, you can create a query that selects the spatial data from the model
        String queryString =
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#> " +
                "PREFIX geojson: <https://purl.org/geojson/vocab#> " +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                "SELECT ?geom " +
                "WHERE { " +
                "  ?s geo:hasGeometry ?geom . " +
                "  ?geom rdf:type geojson:coordinates . " +
                "}";

        String queryS = """
                PREFIX geojson: <https://purl.org/geojson/vocab#>
                                
                SELECT ?coordinates WHERE {
                  ?feature geojson:geometry/geojson:coordinates ?coordinates .
                }
                """;

        Query query = QueryFactory.create(queryS);

// Next, you can execute the query against the model
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            // Finally, you can convert the results to GeoJSON
//            GeoJSONWriter writer = new GeoJSONWriter();
//            FeatureCollection featureCollection = new FeatureCollection();
            while (results.hasNext()) {
                QuerySolution solution = results.next();
                RDFNode coordinates = solution.get("coordinates");
                Iterator<String> stringIterator = solution.varNames();
//                stringIterator.forEachRemaining(System.out::println);
//                System.out.println(stringIterator);
//                Geometry geometry = solution.getLiteral("geom").asLiteral().getValue(Geometry.class);
//                Feature feature = new Feature(writer.write(geometry));
//                featureCollection.add(feature);
            }
//            String geojson = writer.write(featureCollection);
//            System.out.println(geojson);
        }
    }

    @Test
    void fooba4r() {
        Model model = RDFParser.source("geojson.json").lang(Lang.JSONLD).build().toModel();

        Resource geoJsonRes = model.listSubjectsWithProperty(createProperty("https://purl.org/geojson/vocab#geometry")).next();
        Resource geometryRes = geoJsonRes.getPropertyResourceValue(createProperty("https://purl.org/geojson/vocab#geometry"));
        Resource polygonRes = geometryRes.getPropertyResourceValue(createProperty("https://purl.org/geojson/vocab#coordinates"));

        System.out.println("foo");

        // Traverse the polygon vertices and construct the GeoJSON polygon
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        StmtIterator iter = polygonRes.listProperties();
        while (iter.hasNext()) {
            RDFNode node = iter.nextStatement().getObject();
            if (node.isLiteral()) {
                jsonArrayBuilder.add(Double.parseDouble(node.asLiteral().getLexicalForm()));
            } else if (node.isResource()) {
                jsonArrayBuilder.add(traverseCoordinates((Resource) node));
            }
        }

        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add("type", "Polygon");
        jsonObjectBuilder.add("coordinates", jsonArrayBuilder.build());

        JsonObject geoJson = jsonObjectBuilder.build();
        System.out.println(geoJson);

    }

    private JsonArray traverseCoordinates(Resource coordinatesRes) {
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        StmtIterator iter = coordinatesRes.listProperties();
        while (iter.hasNext()) {
            RDFNode node = iter.nextStatement().getObject();
            if (node.isLiteral()) {
                jsonArrayBuilder.add(Double.parseDouble(node.asLiteral().getLexicalForm()));
            } else if (node.isResource()) {
                jsonArrayBuilder.add(traverseCoordinates((Resource) node));
            }
        }
        return jsonArrayBuilder.build();
    }

    @Test
    void geojsonJackson() {
//        GeoJSON
    }
}