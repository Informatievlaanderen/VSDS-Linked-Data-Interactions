package be.vlaanderen.informatievlaanderen.ldes.ldi.converter;

import be.vlaanderen.informatievlaanderen.ldes.ldi.WktConverter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.WktResult;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;

import java.util.List;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;

public class GeoJsonToRdfPlusWktConverter implements GeoJsonConverter {

    private static final String GEOSPARQL_URI = "http://www.opengis.net/ont/geosparql";
    private final WktConverter wktConverter = new WktConverter();

    @Override
    public List<Statement> createNewGeometryStatements(Statement oldStatement, Model geometryModel) {
        final Model geometry = ModelFactory.createDefaultModel();
        final Property geometryPredicate = createProperty("http://www.w3.org/ns/locn#geometry");
        final Resource blankNode = createResource();

        final WktResult wktResult = retrieveWkt(geometryModel);
        final Literal wktLiteral = createTypedLiteralBasedOnWktResult(wktResult);

        addStatementsToModel(oldStatement, geometry, blankNode, wktResult, wktLiteral, geometryPredicate);

        return getStatementsFromModel(geometry);
    }

    private static List<Statement> getStatementsFromModel(Model geometry) {
        return geometry.listStatements().toList();
    }

    private WktResult retrieveWkt(Model geometryModel) {
        return wktConverter.getWktFromModel(geometryModel);
    }

    private Literal createTypedLiteralBasedOnWktResult(WktResult wktResult) {
        return ResourceFactory.createTypedLiteral(wktResult.wkt(), getWktLiteralDataType());
    }

    private void addStatementsToModel(Statement oldStatement,
                                      Model geometry,
                                      Resource blankNode,
                                      WktResult wktResult,
                                      Literal wktLiteral,
                                      Property geometryPredicate) {
        geometry.add(blankNode, RDF.type, createProperty(wktResult.type().getSimpleFeaturesUri()));
        geometry.add(blankNode, createProperty(GEOSPARQL_URI + "#asWKT"), wktLiteral);
        geometry.add(oldStatement.getSubject(), geometryPredicate, blankNode);
    }

}
