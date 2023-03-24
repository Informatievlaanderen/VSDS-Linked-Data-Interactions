package be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;

import java.util.ArrayList;
import java.util.List;

public class LdiMultiLineString {

	private final Model model;
	private final Resource subject;

	public LdiMultiLineString(Model model, Resource subject) {
		this.model = model;
		this.subject = subject;
	}

	public MultiLineString createMultiLineString(GeometryFactory factory) {
		List<List<Coordinate>> coordinates = new LdiPolygon(model, subject, new ArrayList<>()).createCoordinates();
		LineString[] lineStrings = coordinates.stream().map(ls -> ls.toArray(Coordinate[]::new))
				.map(factory::createLineString).toArray(LineString[]::new);
		return factory.createMultiLineString(lineStrings);
	}

}
