package be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;

import java.util.ArrayList;
import java.util.List;

public class LdiLineString {

	private final Model model;
	private final Resource coordinates;
	private final List<Coordinate> result = new ArrayList<>();

	public LdiLineString(Model model, Resource coordinates) {
		this.model = model;
		this.coordinates = coordinates;
	}

	public LineString createLineString(GeometryFactory factory) {
		return factory.createLineString(createCoordinates().toArray(Coordinate[]::new));
	}

	List<Coordinate> createCoordinates() {
		Resource firstPoint = model.listObjectsOfProperty(coordinates, RDF.first).mapWith(RDFNode::asResource).next();
		result.add(new LdiPoint(model, firstPoint).createCoordinate());
		Resource nextPoint = model.listObjectsOfProperty(coordinates, RDF.rest).mapWith(RDFNode::asResource).next();
		if (!RDF.nil.getURI().equals(nextPoint.getURI())) {
			result.addAll(new LdiLineString(model, nextPoint).createCoordinates());
		}
		return result;
	}

}
