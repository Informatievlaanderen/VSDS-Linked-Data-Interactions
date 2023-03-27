package be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class LdiMultiPolygon {

	private final Model model;
	private final Resource subject;
	private final List<List<List<Coordinate>>> result;

	public LdiMultiPolygon(Model model, Resource subject, List<List<List<Coordinate>>> result) {
		this.model = model;
		this.subject = subject;
		this.result = result;
	}

	public MultiPolygon createMultiPolygon(GeometryFactory factory) {
		var multiPolygon = createCoordinates();
		return factory.createMultiPolygon(multiPolygon.stream().map(toPolygon(factory)).toArray(Polygon[]::new));
	}

	private static Function<List<List<Coordinate>>, Polygon> toPolygon(GeometryFactory factory) {
		return polygon -> LdiPolygon.mapToPolygon(polygon, factory);
	}

	List<List<List<Coordinate>>> createCoordinates() {
		Resource firstPolygon = model.listObjectsOfProperty(subject, RDF.first).mapWith(RDFNode::asResource).next();
		result.add(new LdiPolygon(model, firstPolygon, new ArrayList<>()).createCoordinates());
		Resource nextPolygon = model.listObjectsOfProperty(subject, RDF.rest).mapWith(RDFNode::asResource).next();
		return RDF.nil.getURI().equals(nextPolygon.getURI())
				? result
				: new LdiMultiPolygon(model, nextPolygon, result).createCoordinates();
	}

}
