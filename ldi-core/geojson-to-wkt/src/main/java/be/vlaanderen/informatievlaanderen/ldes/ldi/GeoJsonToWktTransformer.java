package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.strategy.GeoJsonConverter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.strategy.GeoJsonToRdfPlusWktConverter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.strategy.GeoJsonToWktConverter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiTransformer;
import org.apache.jena.rdf.model.Model;

import java.util.List;

public class GeoJsonToWktTransformer implements LdiTransformer {

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
	public List<Model> apply(Model model) {
		return List.of(geoJsonConverter.convert(model));
	}

}
