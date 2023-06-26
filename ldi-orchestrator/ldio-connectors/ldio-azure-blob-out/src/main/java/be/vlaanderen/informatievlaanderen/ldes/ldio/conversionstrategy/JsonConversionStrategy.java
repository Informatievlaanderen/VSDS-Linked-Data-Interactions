package be.vlaanderen.informatievlaanderen.ldes.ldio.conversionstrategy;

import be.vlaanderen.informatievlaanderen.ldes.ldio.conversionstrategy.json.Model2JsonConverter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.MemberConversionException;
import org.apache.jena.rdf.model.Model;

import java.io.IOException;

public class JsonConversionStrategy implements ConversionStrategy {

	private final Model2JsonConverter model2JsonConverter;

	public JsonConversionStrategy(String jsonContextURI) {
		model2JsonConverter = new Model2JsonConverter(jsonContextURI);
	}

	@Override
	public String getFileExtension() {
		return "json";
	}

	@Override
	public String getContent(Model model) {
		try {
			return model2JsonConverter.modelToJSONLD(model);
		} catch (IOException e) {
			throw new MemberConversionException(model, e);
		}
	}

}
