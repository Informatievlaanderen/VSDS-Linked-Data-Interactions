package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.strategy.GeoJsonConverter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.strategy.GeoJsonToRdfPlusWktConverter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.strategy.GeoJsonToWktConverter;
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
