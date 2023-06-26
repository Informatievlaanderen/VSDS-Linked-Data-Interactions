package be.vlaanderen.informatievlaanderen.ldes.ldio.conversionstrategy;

import be.vlaanderen.informatievlaanderen.ldes.ldio.util.ModelConverter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;

public class RDFSerializationConversionStrategy implements ConversionStrategy {
	private final Lang lang;

	public RDFSerializationConversionStrategy(Lang lang) {
		this.lang = lang;
	}

	@Override
	public String getFileExtension() {
		return lang.getFileExtensions().get(0);
	}

	@Override
	public String getContent(Model model) {
		return ModelConverter.toString(model, lang);
	}
}
