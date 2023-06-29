package be.vlaanderen.informatievlaanderen.ldes.ldio.conversionstrategy;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;

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
		return RDFWriter.source(model).lang(lang).toString();
	}
}
