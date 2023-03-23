package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiTransformer;
import org.apache.jena.geosparql.implementation.datatype.WKTDatatype;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.WktConverter.GEOJSON_GEOMETRY;

public class GeoJsonToWktTransformer implements LdiTransformer {

	private final WktConverter wktConverter = new WktConverter();

	@Override
	public Model apply(Model model) {
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
		final Literal wktLiteral = ResourceFactory.createTypedLiteral(wktString, WKTDatatype.INSTANCE);
		final Property geometryPredicate = ResourceFactory.createProperty("http://www.w3.org/ns/locn#geometry");
		return ResourceFactory.createStatement(oldStatement.getSubject(), geometryPredicate, wktLiteral);
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
