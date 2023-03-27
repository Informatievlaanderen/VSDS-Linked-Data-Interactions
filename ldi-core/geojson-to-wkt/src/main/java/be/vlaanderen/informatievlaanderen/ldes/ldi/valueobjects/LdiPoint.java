package be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class LdiPoint {

	private final Model model;
	Resource subject;

	public LdiPoint(Model model, Resource subject) {
		this.model = model;
		this.subject = subject;
	}

	public Point createPoint(GeometryFactory factory) {
		return factory.createPoint(createCoordinate());
	}

	Coordinate createCoordinate() {
		double first = model.listObjectsOfProperty(subject, RDF.first).mapWith(RDFNode::asLiteral).next().getDouble();
		Resource restId = model.listObjectsOfProperty(subject, RDF.rest).mapWith(RDFNode::asResource).next();
		double second = model.listObjectsOfProperty(restId, RDF.first).mapWith(RDFNode::asLiteral).next().getDouble();
		return new Coordinate(first, second);
	}

}
