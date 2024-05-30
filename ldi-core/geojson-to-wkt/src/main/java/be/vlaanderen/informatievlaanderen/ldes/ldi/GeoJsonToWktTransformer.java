package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.converter.GeoJsonConverter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.converter.GeoJsonToRdfPlusWktConverter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.converter.GeoJsonToWktConverter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOneToOneTransformer;
import org.apache.jena.rdf.model.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.WktConverter.GEOJSON_GEOMETRY;

/**
 * Ldi Transformer components that is in fact a wrapper around a GeoJsonConverter
 */
public class GeoJsonToWktTransformer implements LdiOneToOneTransformer {

	private final GeoJsonConverter geoJsonConverter;

	/**
	 * Constructs either around a converter that converts the GeoJson to simple WKT or to WKT in RDF format
	 *
	 * @param transformToRdfWkt boolean that determines whether to convert the input to RDF+WKT if true or to simple RDF if false
	 */
	public GeoJsonToWktTransformer(boolean transformToRdfWkt) {
		this.geoJsonConverter = transformToRdfWkt ?
				new GeoJsonToRdfPlusWktConverter() :
				new GeoJsonToWktConverter();
	}

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
			model.remove(createModelWithChildStatements(model, oldGeometryStatement));
			model.add(geoJsonConverter.createNewGeometryStatements(oldGeometryStatement, geometryModel));
		});
		return model;
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
