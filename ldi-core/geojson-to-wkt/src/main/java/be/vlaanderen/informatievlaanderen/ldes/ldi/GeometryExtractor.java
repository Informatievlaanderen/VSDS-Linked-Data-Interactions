package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;
import org.locationtech.jts.geom.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.WktConverter.COORDINATES;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class GeometryExtractor {

	final GeometryFactory factory = new GeometryFactory();

	public Geometry createGeometry(Model model, GeoType type, Resource subject) {
		return switch (type) {
			case POINT -> factory.createPoint(createPointCoordinate(model, subject));
			case LINESTRING -> createLineString(model, subject);
			case POLYGON -> createPolygon(model, subject);
			case MULTIPOINT -> createMultiPoint(model, subject);
			case MULTILINESTRING -> createMultiLineString(model, subject);
			case MULTIPOLYGON -> createMultiPolygon(model, subject);
			case GEOMETRYCOLLECTION -> createGeometryCollection(model, subject);
		};
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
		return factory.createGeometryCollection(createGeometryCollection(model, subject, new ArrayList<>())
				.toArray(Geometry[]::new));
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

	private List<Geometry> createGeometryCollection(Model model, Resource subject, List<Geometry> result) {
		List<Statement> geos = model
				.listStatements(subject, createProperty("https://purl.org/geojson/vocab#geometries"), (RDFNode) null)
				.toList();
		return geos.stream().map(geo -> {
			String type = model.listObjectsOfProperty(geo.getObject().asResource(), RDF.type)
					.mapWith(RDFNode::asResource).mapWith(Resource::getURI).next();
			Statement next = model.listStatements(geo.getObject().asResource(), COORDINATES, (RDFNode) null).next();
			return createGeometry(model, GeoType.fromUri(type).orElseThrow(), next.getObject().asResource());
		}).toList();

		// Resource firstGeo = model.listObjectsOfProperty(subject,
		// RDF.first).mapWith(RDFNode::asResource).next();
		// // result.add(createGeometry(model, firstGeo));
		// Resource nextGeo = model.listObjectsOfProperty(subject,
		// RDF.first).mapWith(RDFNode::asResource).next();
		// if (RDF.nil.getURI().equals(nextGeo.getURI())) {
		// return result;
		// } else {
		// return createGeometryCollection(model, nextGeo, result);
		// }
	}

}
