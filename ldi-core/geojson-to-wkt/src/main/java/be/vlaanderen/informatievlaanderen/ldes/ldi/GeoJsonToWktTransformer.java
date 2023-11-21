package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOneToOneTransformer;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.rdf.model.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.WktConverter.GEOJSON_GEOMETRY;

public class GeoJsonToWktTransformer implements LdiOneToOneTransformer {

	private final WktConverter wktConverter = new WktConverter();

	/**
	 * Replaces all geojson:geometry statements with locn:geometry statements
	 * containing geosparql#wktLiteral
	 */
	@Override
	public Model transform(Model model) {
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
		final String wktString = wktConverter.getWktFromModel(geometryModel);
		final Literal wktLiteral = ResourceFactory.createTypedLiteral(wktString, getWktLiteralDataType());
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
