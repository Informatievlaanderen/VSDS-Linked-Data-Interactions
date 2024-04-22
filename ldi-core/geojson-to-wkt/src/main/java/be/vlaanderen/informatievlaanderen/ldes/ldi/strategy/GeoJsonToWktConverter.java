package be.vlaanderen.informatievlaanderen.ldes.ldi.strategy;

import be.vlaanderen.informatievlaanderen.ldes.ldi.WktConverter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.WktResult;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.rdf.model.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.WktConverter.GEOJSON_GEOMETRY;

public class GeoJsonToWktConverter implements GeoJsonConverter {

    private final WktConverter wktConverter = new WktConverter();

    @Override
    public Model convert(Model model) {
        final List<Statement> geometryStatements = model.listStatements(null, GEOJSON_GEOMETRY, (RDFNode) null)
                .toList();
        geometryStatements.forEach(oldGeometryStatement -> {
            final Model geometryModel = createModelWithChildStatements(model, oldGeometryStatement);
            final Statement newGeometryStatement = createNewGeometryStatement(oldGeometryStatement, geometryModel);
            model.remove(createModelWithChildStatements(model, oldGeometryStatement));
            model.add(newGeometryStatement);
        });
        return model;
    }

    private Statement createNewGeometryStatement(Statement oldStatement, Model geometryModel) {
        final WktResult wktResult = wktConverter.getWktFromModel(geometryModel);
        final Literal wktLiteral = ResourceFactory.createTypedLiteral(wktResult.wkt(), getWktLiteralDataType());
        final Property geometryPredicate = ResourceFactory.createProperty("http://www.w3.org/ns/locn#geometry");
        return ResourceFactory.createStatement(oldStatement.getSubject(), geometryPredicate, wktLiteral);
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
