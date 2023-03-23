package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;
import org.locationtech.jts.geom.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.WktConverter.GEOJSON_GEOMETRY;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class GeometryExtractor {

	public static final Property COORDINATES = createProperty("https://purl.org/geojson/vocab#coordinates");
	public static final Property GEOMETRIES = createProperty("https://purl.org/geojson/vocab#geometries");

	final GeometryFactory factory = new GeometryFactory();

	/**
	 * Extracts a `Geometry` object from a model.
	 * @param model a model containing a geojson:geometry
	 * @param geometrySubject the subject of the geojson:geometry
	 * @return the geojson:geometry mapped to a java Geometry object
	 */
	public Geometry createGeometry(Model model, Resource geometrySubject) {
		final GeoType type = getType(model, geometrySubject);

		final Resource coordinates = GeoType.GEOMETRYCOLLECTION.equals(type)
				? geometrySubject
				: model.listObjectsOfProperty(geometrySubject, COORDINATES).mapWith(RDFNode::asResource).next();

		return switch (type) {
			case POINT -> factory.createPoint(createPointCoordinate(model, coordinates));
			case LINESTRING -> createLineString(model, coordinates);
			case POLYGON -> createPolygon(model, coordinates);
			case MULTIPOINT -> createMultiPoint(model, coordinates);
			case MULTILINESTRING -> createMultiLineString(model, coordinates);
			case MULTIPOLYGON -> createMultiPolygon(model, coordinates);
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

	private LineString createLineString(Model model, Resource subject) {
		List<Coordinate> coordinates = createLineStringCoordinates(model, subject, new ArrayList<>());
		return factory.createLineString(coordinates.toArray(Coordinate[]::new));
	}

	private Polygon createPolygon(Model model, Resource subject) {
		List<List<Coordinate>> coordinates = createPolygonCoordinates(model, subject, new ArrayList<>());
		return mapToPolygon(coordinates);
	}

	private MultiPoint createMultiPoint(Model model, Resource subject) {
		List<Coordinate> coordinates = createLineStringCoordinates(model, subject, new ArrayList<>());
		return factory.createMultiPoint(coordinates.stream().map(factory::createPoint).toArray(Point[]::new));
	}

	private MultiLineString createMultiLineString(Model model, Resource subject) {
		List<List<Coordinate>> coordinates = createPolygonCoordinates(model, subject, new ArrayList<>());
		LineString[] lineStrings = coordinates.stream().map(ls -> ls.toArray(Coordinate[]::new))
				.map(factory::createLineString).toArray(LineString[]::new);
		return factory.createMultiLineString(lineStrings);
	}

	private MultiPolygon createMultiPolygon(Model model, Resource subject) {
		List<List<List<Coordinate>>> multiPolygon = createMultiPolygonCoordinates(model, subject, new ArrayList<>());
		return factory.createMultiPolygon(multiPolygon.stream().map(this::mapToPolygon).toArray(Polygon[]::new));
	}

	private Polygon mapToPolygon(List<List<Coordinate>> coords) {
		List<LinearRing> linearRings = coords.stream().map(l -> factory.createLinearRing(l.toArray(Coordinate[]::new)))
				.collect(Collectors.toList());
		return factory.createPolygon(linearRings.remove(0), linearRings.toArray(LinearRing[]::new));
	}

	private GeometryCollection createGeometryCollection(Model model, Resource subject) {
		List<Statement> geos = model.listStatements(subject, GEOMETRIES, (RDFNode) null).toList();
		var geometries = geos.stream().map(geo -> createGeometry(model, geo.getObject().asResource())).toArray(Geometry[]::new);
		return factory.createGeometryCollection(geometries);
	}

	private Coordinate createPointCoordinate(Model model, Resource subject) {
		double first = model.listObjectsOfProperty(subject, RDF.first).mapWith(RDFNode::asLiteral).next().getDouble();
		Resource restId = model.listObjectsOfProperty(subject, RDF.rest).mapWith(RDFNode::asResource).next();
		double second = model.listObjectsOfProperty(restId, RDF.first).mapWith(RDFNode::asLiteral).next().getDouble();
		return new Coordinate(first, second);
	}

	private List<Coordinate> createLineStringCoordinates(Model model, Resource coordinates, List<Coordinate> result) {
		Resource firstPoint = model.listObjectsOfProperty(coordinates, RDF.first).mapWith(RDFNode::asResource).next();
		result.add(createPointCoordinate(model, firstPoint));
		Resource nextPoint = model.listObjectsOfProperty(coordinates, RDF.rest).mapWith(RDFNode::asResource).next();
		return RDF.nil.getURI().equals(nextPoint.getURI())
				? result
				: createLineStringCoordinates(model, nextPoint, result);
	}

	private List<List<Coordinate>> createPolygonCoordinates(Model model, Resource subject,
			List<List<Coordinate>> result) {
		Resource firstRing = model.listObjectsOfProperty(subject, RDF.first).mapWith(RDFNode::asResource).next();
		result.add(createLineStringCoordinates(model, firstRing, new ArrayList<>()));
		Resource nextRing = model.listObjectsOfProperty(subject, RDF.rest).mapWith(RDFNode::asResource).next();
		return RDF.nil.getURI().equals(nextRing.getURI())
				? result
				: createPolygonCoordinates(model, nextRing, result);
	}

	private List<List<List<Coordinate>>> createMultiPolygonCoordinates(Model model, Resource subject,
			List<List<List<Coordinate>>> result) {
		Resource firstPolygon = model.listObjectsOfProperty(subject, RDF.first).mapWith(RDFNode::asResource).next();
		result.add(createPolygonCoordinates(model, firstPolygon, new ArrayList<>()));
		Resource nextPolygon = model.listObjectsOfProperty(subject, RDF.rest).mapWith(RDFNode::asResource).next();
		return RDF.nil.getURI().equals(nextPolygon.getURI())
				? result
				: createMultiPolygonCoordinates(model, nextPolygon, result);
	}

}
