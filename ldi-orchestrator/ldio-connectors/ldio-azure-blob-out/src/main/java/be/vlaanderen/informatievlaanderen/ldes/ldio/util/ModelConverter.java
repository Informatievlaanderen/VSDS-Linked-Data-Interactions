package be.vlaanderen.informatievlaanderen.ldes.ldio.util;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import java.io.StringWriter;

public class ModelConverter {

	private ModelConverter() {
		// static utility class
	}

	public static String toString(final Model model, final Lang lang) {
		StringWriter stringWriter = new StringWriter();
		RDFDataMgr.write(stringWriter, model, lang);
		return stringWriter.toString();
	}
}
