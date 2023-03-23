package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTWriter;

import java.util.List;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class WktConverter {

	public static final Property GEOJSON_GEOMETRY = createProperty("https://purl.org/geojson/vocab#geometry");
	public static final Property COORDINATES = createProperty("https://purl.org/geojson/vocab#coordinates");
	public static final Property GEOMETRIES = createProperty("https://purl.org/geojson/vocab#geometries");

	private final GeometryExtractor geometryExtractor = new GeometryExtractor();

	public String getWktFromModel(Model model) {
		final Resource geometryId = getGeometryId(model);
		final GeoType type = getType(model, geometryId);
		Resource coordinatesNode = model.listObjectsOfProperty(geometryId, COORDINATES).mapWith(RDFNode::asResource)
				.next();
		final Geometry geom = geometryExtractor.createGeometry(model, type, coordinatesNode);
		return new WKTWriter().write(geom);
	}

	private Resource getGeometryId(Model model) {
		final List<Resource> geometryStatements = model.listObjectsOfProperty(GEOJSON_GEOMETRY)
				.mapWith(RDFNode::asResource).toList();

		if (geometryStatements.size() > 1) {
			throw new IllegalArgumentException("Ambiguous request: multiple geojson:geometry found.");
		}

		return geometryStatements.get(0);
	}

	private GeoType getType(Model model, Resource geometryId) {
		final List<RDFNode> typeList = model.listObjectsOfProperty(geometryId, RDF.type).toList();
		if (typeList.size() != 1) {
			final String errorMsg = "Could not determine %s of %s".formatted(RDF.type.getURI(),
					GEOJSON_GEOMETRY.getURI());
			throw new IllegalArgumentException(errorMsg);
		}

		final String type = typeList.get(0).asResource().getURI();
		return GeoType
				.fromUri(type)
				.orElseThrow(() -> new IllegalArgumentException("Geotype %s not supported".formatted(type)));
	}

}
