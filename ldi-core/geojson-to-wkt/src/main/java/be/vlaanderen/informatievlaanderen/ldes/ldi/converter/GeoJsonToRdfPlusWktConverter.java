package be.vlaanderen.informatievlaanderen.ldes.ldi.converter;

import be.vlaanderen.informatievlaanderen.ldes.ldi.WktConverter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.WktResult;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.WktConverter.GEOJSON_GEOMETRY;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;

public class GeoJsonToRdfPlusWktConverter implements GeoJsonConverter {

    private final WktConverter wktConverter = new WktConverter();

    @Override
    public Model convert(Model model) {
        final List<Statement> geometryStatements = model.listStatements(null, GEOJSON_GEOMETRY, (RDFNode) null)
                .toList();
        geometryStatements.forEach(oldGeometryStatement -> {
            final Model geometryModel = createModelWithChildStatements(model, oldGeometryStatement);
            model.remove(createModelWithChildStatements(model, oldGeometryStatement));
            model.add(createNewGeometryStatements(oldGeometryStatement, geometryModel));
        });
        return model;
    }

    private Model createNewGeometryStatements(Statement oldStatement, Model geometryModel) {
        final WktResult wktResult = wktConverter.getWktFromModel(geometryModel);
        final Model geometry = ModelFactory.createDefaultModel();
        final Resource blankNode = createResource();
        geometry.add(blankNode, RDF.type, createProperty(wktResult.type().getUri()));
        final Literal wktLiteral = ResourceFactory.createTypedLiteral(wktResult.wkt(), getWktLiteralDataType());
        geometry.add(blankNode, createProperty("http://www.w3.org/ns/locn#asWKT"), wktLiteral);
        final Property geometryPredicate = createProperty("http://www.w3.org/ns/locn#geometry");
        geometry.add(oldStatement.getSubject(), geometryPredicate, blankNode);
        return geometry;
    }

    private RDFDatatype getWktLiteralDataType() {
        return TypeMapper.getInstance().getSafeTypeByName("http://www.opengis.net/ont/geosparql#wktLiteral");
    }

    private Model createModelWithChildStatements(Model model, Statement statement) {
        final Set<Statement> statements = new HashSet<>();
        statements.add(statement);
        addChildStatements(model, statement.getObject().asResource(), statements);
        return ModelFactory.createDefaultModel().add(statements.toArray(Statement[]::new));
    }

    private void addChildStatements(Model model, Resource subject, Set<Statement> statements) {
        StmtIterator stmtIterator = model.listStatements(subject, null, (RDFNode) null);

        stmtIterator.forEach(statement -> {
            if (statement.getObject().isAnon()) {
                addChildStatements(model, statement.getObject().asResource(), statements);
            }
            statements.add(statement);
        });
    }

}
