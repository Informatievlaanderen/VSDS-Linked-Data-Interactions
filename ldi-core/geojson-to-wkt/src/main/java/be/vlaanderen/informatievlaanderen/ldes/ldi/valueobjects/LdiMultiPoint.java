package be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;

import java.util.List;

public class LdiMultiPoint {

	private final Model model;
	private final Resource subject;

	public LdiMultiPoint(Model model, Resource subject) {
		this.model = model;
		this.subject = subject;
	}

	public MultiPoint createMultiPoint(GeometryFactory factory) {
		List<Coordinate> coordinates = new LdiLineString(model, subject).createCoordinates();
		return factory.createMultiPoint(coordinates.stream().map(factory::createPoint).toArray(Point[]::new));
	}

}
