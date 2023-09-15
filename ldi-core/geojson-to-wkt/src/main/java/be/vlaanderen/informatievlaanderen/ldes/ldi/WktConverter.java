package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.GeoType;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTWriter;

import java.util.List;
import java.util.Optional;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class WktConverter {

	public static final Property GEOJSON_GEOMETRY = createProperty("https://purl.org/geojson/vocab#geometry");

	private final GeometryExtractor geometryExtractor = new GeometryExtractor();
	private final WKTWriter writer = new WKTWriter();

	/**
	 * Locates the geojson:geometry from the provided model and returns the WKT
	 * translation
	 */
	public WktResult getWktFromModel(Model model) {
		final Geometry geom = geometryExtractor.createGeometry(model, getGeometryId(model));
		GeoType geoType = GeoType.fromName(geom.getGeometryType()).orElseThrow(); // TODO TVB: 15/09/23 throw custom issue
		return new WktResult(geoType, writer.write(geom));
	}

	private Resource getGeometryId(Model model) {
		final List<Resource> geometryStatements = model.listObjectsOfProperty(GEOJSON_GEOMETRY)
				.mapWith(RDFNode::asResource).toList();

		if (geometryStatements.size() > 1) {
			throw new IllegalArgumentException("Ambiguous request: multiple geojson:geometry found.");
		}

		return geometryStatements.get(0);
	}

}
