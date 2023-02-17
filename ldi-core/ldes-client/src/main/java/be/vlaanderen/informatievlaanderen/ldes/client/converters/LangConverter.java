package be.vlaanderen.informatievlaanderen.ldes.client.converters;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;

public class LangConverter {

	private LangConverter() {
	}

	public static Lang convertToLang(String lang) {
		return RDFLanguages.nameToLang(lang);
	}
}
