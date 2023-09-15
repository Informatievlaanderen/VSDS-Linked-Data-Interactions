package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiTransformer;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.WktConverter.GEOJSON_GEOMETRY;
import static org.apache.jena.rdf.model.ResourceFactory.*;

public class GeoJsonToWktTransformer implements LdiTransformer {

	private final WktConverter wktConverter = new WktConverter();

	/**
	 * Replaces all geojson:geometry statements with locn:geometry statements
	 * containing geosparql#wktLiteral
	 */
	@Override
	public List<Model> apply(Model model) {
		final List<Statement> geometryStatements = model.listStatements(null, GEOJSON_GEOMETRY, (RDFNode) null)
				.toList();
		geometryStatements.forEach(oldGeometryStatement -> {
			final Model geometryModel = createModelWithChildStatements(model, oldGeometryStatement);
			model.remove(createModelWithChildStatements(model, oldGeometryStatement));
			model.add(createNewGeometryStatements(oldGeometryStatement, geometryModel));
		});
		return List.of(model);
	}

	// TODO TVB: 15/09/23 source https://semiceu.github.io/Core-Location-Vocabulary/releases/w3c/#locn:geometry
	// RDF+WKT (GeoSPARQL)
	// TODO TVB: 15/09/23 cleanup below code
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
