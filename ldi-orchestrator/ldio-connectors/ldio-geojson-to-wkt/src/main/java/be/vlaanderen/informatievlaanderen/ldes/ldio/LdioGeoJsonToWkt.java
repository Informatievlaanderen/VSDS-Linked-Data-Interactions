package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.GeoJsonToWktTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioTransformer;
import org.apache.jena.rdf.model.Model;

public class LdioGeoJsonToWkt extends LdioTransformer {
	public static final String NAME = "Ldio:GeoJsonToWktTransformer";
	private final GeoJsonToWktTransformer transformer;

	public LdioGeoJsonToWkt(boolean transformToRdfWkt) {
		this.transformer = new GeoJsonToWktTransformer(transformToRdfWkt);
	}

	@Override
	public void apply(Model model) {
		this.next(transformer.transform(model));
	}
}
