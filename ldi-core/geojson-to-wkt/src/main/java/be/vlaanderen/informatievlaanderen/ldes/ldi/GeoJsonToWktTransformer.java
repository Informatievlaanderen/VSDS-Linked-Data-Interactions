package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.converter.GeoJsonConverter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.converter.GeoJsonToRdfPlusWktConverter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.converter.GeoJsonToWktConverter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOneToOneTransformer;
import org.apache.jena.rdf.model.Model;

public class GeoJsonToWktTransformer implements LdiOneToOneTransformer {

	private final GeoJsonConverter geoJsonConverter;

	public GeoJsonToWktTransformer(boolean rdfPlusWktEnabled) {
		this.geoJsonConverter = rdfPlusWktEnabled ?
				new GeoJsonToRdfPlusWktConverter() :
				new GeoJsonToWktConverter();
	}

	/**
	 * Replaces all geojson:geometry statements with locn:geometry statements
	 * containing geosparql#wktLiteral
	 */
	@Override
	public Model transform(Model model) {
		return geoJsonConverter.convert(model);
	}
}
