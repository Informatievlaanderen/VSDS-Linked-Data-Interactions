package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class MyWktConverter {

    public static final String RDF_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public static final Property RDF_TYPE = createProperty(RDF_URI + "type");
    public static final Property RDF_FIRST = createProperty(RDF_URI + "first");
    public static final Property RDF_REST = createProperty(RDF_URI + "rest");
    public static final Property RDF_NIL = createProperty(RDF_URI + "nil");
    public static final Property GEOMETRY = createProperty("https://purl.org/geojson/vocab#geometry");
    public static final Property COORDINATES = createProperty("https://purl.org/geojson/vocab#coordinates");

    final GeometryFactory factory = new GeometryFactory();

    public String getWktFromModel(Model model) {
        final Resource geometryId = getGeometryId(model);
        final GeoType type = getType(model, geometryId);

        Resource coordinates = model.listStatements(geometryId, COORDINATES, (RDFNode) null).nextStatement().getObject().asResource();

        final Geometry geom = switch (type) {
            case POINT -> {
                Coordinate point = createPoint(model, coordinates);
                yield factory.createPoint(point);
            }
            case LINESTRING -> {
                List<Coordinate> result = new ArrayList<>();
                List<Coordinate> lineString = createLineString(model, coordinates, result);
                yield factory.createLineString(lineString.toArray(new Coordinate[0]));
            }
            case POLYGON -> {
                List<List<Coordinate>> result = new ArrayList<>();
                List<List<Coordinate>> lineString = createPolygon(model, coordinates, result);
                List<LinearRing> linearRings = lineString.stream().map(l -> factory.createLinearRing(l.toArray(new Coordinate[0]))).collect(Collectors.toList());
                yield  factory.createPolygon(linearRings.remove(0), linearRings.toArray(new LinearRing[0]));
            }
            case MULTIPOINT -> {
                List<Coordinate> result = new ArrayList<>();
                List<Coordinate> lineString = createLineString(model, coordinates, result);
                yield factory.createMultiPoint(lineString.stream().map(factory::createPoint).toArray(Point[]::new));
            }
            case MULTILINESTRING -> {
                List<List<Coordinate>> result = new ArrayList<>();
                List<List<Coordinate>> lineString = createPolygon(model, coordinates, result);
                LineString[] lineStrings = lineString.stream().map(ls -> ls.toArray(Coordinate[]::new)).map(factory::createLineString).toArray(LineString[]::new);
                yield factory.createMultiLineString(lineStrings);
            }
            case MULTIPOLYGON -> null;
            case GEOMETRYCOLLECTION -> null;
        };

        return new WKTWriter().write(geom);
    }

    private Coordinate createPoint(Model model, Resource coordinates) {
        double first = model.listStatements(coordinates, RDF_FIRST, (RDFNode) null).nextStatement().getObject().asLiteral().getDouble();
        Resource restId = model.listStatements(coordinates, RDF_REST, (RDFNode) null).nextStatement().getObject().asResource();
        double second = model.listStatements(restId, RDF_FIRST, (RDFNode) null).nextStatement().getObject().asLiteral().getDouble();
        return new Coordinate(first, second);
    }

    private List<Coordinate> createLineString(Model model, Resource coordinates, List<Coordinate> result) {
        Resource firstPoint = model.listStatements(coordinates, RDF_FIRST, (RDFNode) null).nextStatement().getObject().asResource();
        result.add(createPoint(model, firstPoint));
        Resource nextPoint = model.listStatements(coordinates, RDF_REST, (RDFNode) null).nextStatement().getObject().asResource();
        if (RDF_NIL.getURI().equals(nextPoint.getURI())) {
            return result;
        } else {
            return createLineString(model, nextPoint, result);
        }
    }

    private List<List<Coordinate>> createPolygon(Model model, Resource coordinates, List<List<Coordinate>> result) {
        Resource exteriorRing = model.listStatements(coordinates, RDF_FIRST, (RDFNode) null).nextStatement().getObject().asResource();
        List<Coordinate> exRing = new ArrayList<>();
        result.add(createLineString(model, exteriorRing, exRing));
        Resource nextRing = model.listStatements(coordinates, RDF_REST, (RDFNode) null).nextStatement().getObject().asResource();
        if (RDF_NIL.getURI().equals(nextRing.getURI())) {
            return result;
        } else {
            return createPolygon(model, nextRing, result);
        }
    }

    private GeoType getType(Model geojson, Resource geometryId) {
        final List<Statement> typeList = geojson.listStatements(geometryId, RDF_TYPE, (RDFNode) null).toList();
        if (typeList.size() != 1) {
            final String errorMsg = "Could not determine %s of %s".formatted(RDF_TYPE.getURI(), GEOMETRY.getURI());
            throw new IllegalArgumentException(errorMsg);
        }

        final String type = typeList.get(0).getObject().asResource().getURI();
        return GeoType.fromUri(type)
                .orElseThrow(() -> new IllegalArgumentException("Geotype %s not supported".formatted(type)));
    }

    private Resource getGeometryId(Model geojson) {
        return geojson.listStatements(null, createProperty("https://purl.org/geojson/vocab#geometry"), (RDFNode) null)
                .toList()
                .stream()
                .map(Statement::getObject)
                .map(RDFNode::asResource)
                .findFirst()
                .orElseThrow();
    }

}
