package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.WKTWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class WktConverter {

    public static final Property GEOMETRY = createProperty("https://purl.org/geojson/vocab#geometry");
    public static final Property COORDINATES = createProperty("https://purl.org/geojson/vocab#coordinates");

    final GeometryFactory factory = new GeometryFactory();

    /**
     * Takes a model with one 'geometry' property and returns geosparql:asWKT String value that can be used to
     * replace the 'geometry.coordinates' node.
     */
    public String getWktFromModel(Model model) {
        final Resource geometryId = getGeometryId(model);
        final GeoType type = getType(model, geometryId);

        Resource coordinatesNode = model.listStatements(geometryId, COORDINATES, (RDFNode) null).nextStatement().getObject().asResource();

        final Geometry geom = switch (type) {
            case POINT -> {
                Coordinate point = createPoint(model, coordinatesNode);
                yield factory.createPoint(point);
            }
            case LINESTRING -> {
                List<Coordinate> result = new ArrayList<>();
                List<Coordinate> lineString = createLineString(model, coordinatesNode, result);
                yield factory.createLineString(lineString.toArray(new Coordinate[0]));
            }
            case POLYGON -> {
                List<List<Coordinate>> result = new ArrayList<>();
                List<List<Coordinate>> lineString = createPolygon(model, coordinatesNode, result);
                yield mapToPolygon(lineString);
            }
            case MULTIPOINT -> {
                List<Coordinate> result = new ArrayList<>();
                List<Coordinate> lineString = createLineString(model, coordinatesNode, result);
                yield factory.createMultiPoint(lineString.stream().map(factory::createPoint).toArray(Point[]::new));
            }
            case MULTILINESTRING -> {
                List<List<Coordinate>> result = new ArrayList<>();
                List<List<Coordinate>> lineString = createPolygon(model, coordinatesNode, result);
                LineString[] lineStrings = lineString.stream().map(ls -> ls.toArray(Coordinate[]::new)).map(factory::createLineString).toArray(LineString[]::new);
                yield factory.createMultiLineString(lineStrings);
            }
            case MULTIPOLYGON -> {
                List<List<List<Coordinate>>> multiPolygon = createMultiPolygon(model, coordinatesNode, new ArrayList<>());
                yield factory.createMultiPolygon(multiPolygon.stream().map(this::mapToPolygon).toArray(Polygon[]::new));
            }
            case GEOMETRYCOLLECTION -> null;
        };

        return new WKTWriter().write(geom);
    }

    private Polygon mapToPolygon(List<List<Coordinate>> coords) {
        List<LinearRing> linearRings = coords.stream().map(l -> factory.createLinearRing(l.toArray(new Coordinate[0]))).collect(Collectors.toList());
        return factory.createPolygon(linearRings.remove(0), linearRings.toArray(new LinearRing[0]));
    }

    private Coordinate createPoint(Model model, Resource coordinates) {
        double first = model.listStatements(coordinates, RDF.first, (RDFNode) null).nextStatement().getObject().asLiteral().getDouble();
        Resource restId = model.listStatements(coordinates, RDF.rest, (RDFNode) null).nextStatement().getObject().asResource();
        double second = model.listStatements(restId, RDF.first, (RDFNode) null).nextStatement().getObject().asLiteral().getDouble();
        return new Coordinate(first, second);
    }

    private List<Coordinate> createLineString(Model model, Resource coordinates, List<Coordinate> result) {
        Resource firstPoint = model.listStatements(coordinates, RDF.first, (RDFNode) null).nextStatement().getObject().asResource();
        result.add(createPoint(model, firstPoint));
        Resource nextPoint = model.listStatements(coordinates, RDF.rest, (RDFNode) null).nextStatement().getObject().asResource();
        if (RDF.nil.getURI().equals(nextPoint.getURI())) {
            return result;
        } else {
            return createLineString(model, nextPoint, result);
        }
    }

    private List<List<Coordinate>> createPolygon(Model model, Resource coordinates, List<List<Coordinate>> result) {
        Resource exteriorRing = model.listStatements(coordinates, RDF.first, (RDFNode) null).nextStatement().getObject().asResource();
        List<Coordinate> exRing = new ArrayList<>();
        result.add(createLineString(model, exteriorRing, exRing));
        Resource nextRing = model.listStatements(coordinates, RDF.rest, (RDFNode) null).nextStatement().getObject().asResource();
        if (RDF.nil.getURI().equals(nextRing.getURI())) {
            return result;
        } else {
            return createPolygon(model, nextRing, result);
        }
    }

    private List<List<List<Coordinate>>> createMultiPolygon(Model model, Resource coordinates, List<List<List<Coordinate>>> result) {
        Resource firstPolygon = model.listStatements(coordinates, RDF.first, (RDFNode) null).nextStatement().getObject().asResource();
        result.add(createPolygon(model, firstPolygon, new ArrayList<>()));
        Resource nextPolygon = model.listStatements(coordinates, RDF.rest, (RDFNode) null).nextStatement().getObject().asResource();
        if (RDF.nil.getURI().equals(nextPolygon.getURI())) {
            return result;
        } else {
            return createMultiPolygon(model, nextPolygon, result);
        }
    }

    private GeoType getType(Model geojson, Resource geometryId) {
        final List<Statement> typeList = geojson.listStatements(geometryId, RDF.type, (RDFNode) null).toList();
        if (typeList.size() != 1) {
            final String errorMsg = "Could not determine %s of %s".formatted(RDF.type.getURI(), GEOMETRY.getURI());
            throw new IllegalArgumentException(errorMsg);
        }

        final String type = typeList.get(0).getObject().asResource().getURI();
        return GeoType.fromUri(type)
                .orElseThrow(() -> new IllegalArgumentException("Geotype %s not supported".formatted(type)));
    }

    private Resource getGeometryId(Model geojson) {
        // TODO: 22/03/2023 add check there is only one?
        return geojson.listStatements(null, GEOMETRY, (RDFNode) null)
                .toList()
                .stream()
                .map(Statement::getObject)
                .map(RDFNode::asResource)
                .findFirst()
                .orElseThrow();
    }

}
