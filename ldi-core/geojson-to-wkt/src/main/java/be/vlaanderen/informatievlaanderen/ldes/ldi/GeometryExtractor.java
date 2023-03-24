package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;
import org.locationtech.jts.geom.*;

import java.util.ArrayList;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.WktConverter.GEOJSON_GEOMETRY;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class GeometryExtractor {

	public static final Property COORDINATES = createProperty("https://purl.org/geojson/vocab#coordinates");
	public static final Property GEOMETRIES = createProperty("https://purl.org/geojson/vocab#geometries");

	final GeometryFactory factory = new GeometryFactory();

	/**
	 * Extracts a `Geometry` object from a model.
	 *
	 * @param model
	 *            a model containing a geojson:geometry
	 * @param geometrySubject
	 *            the subject of the geojson:geometry
	 * @return the geojson:geometry mapped to a java Geometry object
	 */
	public Geometry createGeometry(Model model, Resource geometrySubject) {
		final GeoType type = getType(model, geometrySubject);

		final Resource coordinates = GeoType.GEOMETRYCOLLECTION.equals(type)
				? geometrySubject
				: model.listObjectsOfProperty(geometrySubject, COORDINATES).mapWith(RDFNode::asResource).next();

		return switch (type) {
			case POINT -> new LdiPoint(model, coordinates).createPoint(factory);
			case LINESTRING -> new LdiLineString(model, coordinates).createLineString(factory);
			case POLYGON -> new LdiPolygon(model, coordinates, new ArrayList<>()).createPolygon(factory);
			case MULTIPOINT -> new LdiMultiPoint(model, coordinates).createMultiPoint(factory);
			case MULTILINESTRING -> new LdiMultiLineString(model, coordinates).createMultiLineString(factory);
			case MULTIPOLYGON -> new LdiMultiPolygon(model, coordinates, new ArrayList<>()).createMultiPolygon(factory);
			case GEOMETRYCOLLECTION -> createGeometryCollection(model, coordinates);
		};
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

	private GeometryCollection createGeometryCollection(Model model, Resource subject) {
		var geometries = model.listStatements(subject, GEOMETRIES, (RDFNode) null).toList()
				.stream()
				.map(geo -> createGeometry(model, geo.getObject().asResource()))
				.toArray(Geometry[]::new);

		return factory.createGeometryCollection(geometries);
	}

}
