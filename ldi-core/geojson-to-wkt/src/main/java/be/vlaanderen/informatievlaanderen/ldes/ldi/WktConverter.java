package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.WKTWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class WktConverter {

	public static final Property GEOJSON_GEOMETRY = createProperty("https://purl.org/geojson/vocab#geometry");
	public static final Property COORDINATES = createProperty("https://purl.org/geojson/vocab#coordinates");
	public static final Property GEOMETRIES = createProperty("https://purl.org/geojson/vocab#geometries");

	final GeometryFactory factory = new GeometryFactory();

	/**
	 * Takes a model with one 'geometry' property and returns geosparql:asWKT String
	 * value that can be used to
	 * replace the 'geometry.coordinates' node.
	 */
	public String getWktFromModel(Model model) {
		final Resource geometryId = getGeometryId(model);
		final GeoType type = getType(model, geometryId);
		Resource coordinatesNode =
				model.listStatements(geometryId, COORDINATES, (RDFNode) null).nextStatement().getObject().asResource();
		final Geometry geom = createGeometry(model, type, coordinatesNode);
		return new WKTWriter().write(geom);
	}

	private Geometry createGeometry(Model model, GeoType type, Resource coordinatesNode) {
		return switch (type) {
			case POINT -> factory.createPoint(createPointCoordinate(model, coordinatesNode));
			case LINESTRING -> createLineString(model, coordinatesNode);
			case POLYGON -> createPolygon(model, coordinatesNode);
			case MULTIPOINT -> createMultiPoint(model, coordinatesNode);
			case MULTILINESTRING -> createMultiLineString(model, coordinatesNode);
			case MULTIPOLYGON -> createMultiPolygon(model, coordinatesNode);
			case GEOMETRYCOLLECTION -> createGeometryCollection(model, coordinatesNode);
		};
	}

	private GeometryCollection createGeometryCollection(Model model, Resource coordinatesNode) {
		return factory.createGeometryCollection(createGeometryCollection(model, coordinatesNode, new ArrayList<>())
				.toArray(Geometry[]::new));
	}

	private MultiPolygon createMultiPolygon(Model model, Resource coordinatesNode) {
		List<List<List<Coordinate>>> multiPolygon = createMultiPolygon(model, coordinatesNode, new ArrayList<>());
		return factory.createMultiPolygon(multiPolygon.stream().map(this::mapToPolygon).toArray(Polygon[]::new));
	}

	private MultiLineString createMultiLineString(Model model, Resource coordinatesNode) {
		List<List<Coordinate>> lineString = createPolygon(model, coordinatesNode, new ArrayList<>());
		LineString[] lineStrings = lineString.stream().map(ls -> ls.toArray(Coordinate[]::new))
				.map(factory::createLineString).toArray(LineString[]::new);
		return factory.createMultiLineString(lineStrings);
	}

	private MultiPoint createMultiPoint(Model model, Resource coordinatesNode) {
		List<Coordinate> lineString = createLineStringCoordinates(model, coordinatesNode, new ArrayList<>());
		return factory.createMultiPoint(lineString.stream().map(factory::createPoint).toArray(Point[]::new));
	}

	private Polygon createPolygon(Model model, Resource coordinatesNode) {
		List<List<Coordinate>> lineString = createPolygon(model, coordinatesNode, new ArrayList<>());
		return mapToPolygon(lineString);
	}

	private LineString createLineString(Model model, Resource coordinatesNode) {
		List<Coordinate> coordinates = createLineStringCoordinates(model, coordinatesNode, new ArrayList<>());
		return factory.createLineString(coordinates.toArray(Coordinate[]::new));
	}

	private Polygon mapToPolygon(List<List<Coordinate>> coords) {
		List<LinearRing> linearRings = coords.stream().map(l -> factory.createLinearRing(l.toArray(Coordinate[]::new)))
				.collect(Collectors.toList());
		return factory.createPolygon(linearRings.remove(0), linearRings.toArray(LinearRing[]::new));
	}

	private Coordinate createPointCoordinate(Model model, Resource subject) {
		double first = model.listObjectsOfProperty(subject, RDF.first).mapWith(RDFNode::asLiteral).next().getDouble();
		Resource restId = model.listObjectsOfProperty(subject, RDF.rest).mapWith(RDFNode::asResource).next();
		double second = model.listObjectsOfProperty(restId, RDF.first).mapWith(RDFNode::asLiteral).next().getDouble();
		return new Coordinate(first, second);
	}

	private List<Coordinate> createLineStringCoordinates(Model model, Resource coordinates, List<Coordinate> result) {
		Resource firstPoint = model.listStatements(coordinates, RDF.first, (RDFNode) null).nextStatement().getObject().asResource();
		result.add(createPointCoordinate(model, firstPoint));
		Resource nextPoint = model.listStatements(coordinates, RDF.rest, (RDFNode) null).nextStatement().getObject().asResource();
		if (RDF.nil.getURI().equals(nextPoint.getURI())) {
			return result;
		} else {
			return createLineStringCoordinates(model, nextPoint, result);
		}
	}

	private List<List<Coordinate>> createPolygon(Model model, Resource coordinates, List<List<Coordinate>> result) {
		Resource exteriorRing = model.listStatements(coordinates, RDF.first, (RDFNode) null).nextStatement().getObject()
				.asResource();
		List<Coordinate> exRing = new ArrayList<>();
		result.add(createLineStringCoordinates(model, exteriorRing, exRing));
		Resource nextRing = model.listStatements(coordinates, RDF.rest, (RDFNode) null).nextStatement().getObject()
				.asResource();
		if (RDF.nil.getURI().equals(nextRing.getURI())) {
			return result;
		} else {
			return createPolygon(model, nextRing, result);
		}
	}

	private List<List<List<Coordinate>>> createMultiPolygon(Model model, Resource coordinates,
			List<List<List<Coordinate>>> result) {
		Resource firstPolygon = model.listStatements(coordinates, RDF.first, (RDFNode) null).nextStatement().getObject()
				.asResource();
		result.add(createPolygon(model, firstPolygon, new ArrayList<>()));
		Resource nextPolygon = model.listStatements(coordinates, RDF.rest, (RDFNode) null).nextStatement().getObject()
				.asResource();
		if (RDF.nil.getURI().equals(nextPolygon.getURI())) {
			return result;
		} else {
			return createMultiPolygon(model, nextPolygon, result);
		}
	}

	private List<Geometry> createGeometryCollection(Model model, Resource coordinates, List<Geometry> result) {
		Resource firstGeo = model.listStatements(coordinates, RDF.first, (RDFNode) null).nextStatement().getObject()
				.asResource();
//		result.add(createGeometry(model, firstGeo));
		Resource nextGeo = model.listStatements(coordinates, RDF.first, (RDFNode) null).nextStatement().getObject()
				.asResource();
		if (RDF.nil.getURI().equals(nextGeo.getURI())) {
			return result;
		} else {
			return createGeometryCollection(model, nextGeo, result);
		}
	}

	private GeoType getType(Model geojson, Resource geometryId) {
		final List<Statement> typeList = geojson.listStatements(geometryId, RDF.type, (RDFNode) null).toList();
		if (typeList.size() != 1) {
			final String errorMsg = "Could not determine %s of %s".formatted(RDF.type.getURI(), GEOJSON_GEOMETRY.getURI());
			throw new IllegalArgumentException(errorMsg);
		}

		final String type = typeList.get(0).getObject().asResource().getURI();
		return GeoType.fromUri(type)
				.orElseThrow(() -> new IllegalArgumentException("Geotype %s not supported".formatted(type)));
	}

	private Resource getGeometryId(Model geojson) {
		// TODO: 22/03/2023 add check there is only one?
		return geojson.listStatements(null, GEOJSON_GEOMETRY, (RDFNode) null)
				.toList()
				.stream()
				.map(Statement::getObject)
				.map(RDFNode::asResource)
				.findFirst()
				.orElseThrow();
	}

}
