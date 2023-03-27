package be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LdiPolygon {

	private final Model model;
	private final Resource subject;
	private final List<List<Coordinate>> result;

	public LdiPolygon(Model model, Resource subject, List<List<Coordinate>> result) {
		this.model = model;
		this.subject = subject;
		this.result = result;
	}

	public Polygon createPolygon(GeometryFactory factory) {
		List<List<Coordinate>> coordinates = new LdiPolygon(model, subject, new ArrayList<>()).createCoordinates();
		return mapToPolygon(coordinates, factory);
	}

	static Polygon mapToPolygon(List<List<Coordinate>> coords, GeometryFactory factory) {
		List<LinearRing> linearRings = coords.stream().map(l -> factory.createLinearRing(l.toArray(Coordinate[]::new)))
				.collect(Collectors.toList());
		return factory.createPolygon(linearRings.remove(0), linearRings.toArray(LinearRing[]::new));
	}

	List<List<Coordinate>> createCoordinates() {
		Resource firstRing = model.listObjectsOfProperty(subject, RDF.first).mapWith(RDFNode::asResource).next();
		result.add(new LdiLineString(model, firstRing).createCoordinates());
		Resource nextRing = model.listObjectsOfProperty(subject, RDF.rest).mapWith(RDFNode::asResource).next();
		return RDF.nil.getURI().equals(nextRing.getURI())
				? result
				: new LdiPolygon(model, nextRing, result).createCoordinates();
	}

}
