package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.GeoJsonToWktTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioProcessor;
import org.apache.jena.rdf.model.Model;

public class LdioGeoJsonToWkt extends LdioProcessor {
	private final GeoJsonToWktTransformer transformer;

	public LdioGeoJsonToWkt() {
		this.transformer = new GeoJsonToWktTransformer();
	}

	@Override
	public void apply(Model model) {
		this.next(transformer.transform(model));
	}
}
