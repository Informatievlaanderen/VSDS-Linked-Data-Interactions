package be.vlaanderen.informatievlaanderen.ldes.client.converters;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFParserBuilder;

import java.io.StringWriter;

public class ModelConverter {

	private ModelConverter() {
	}

	public static Model convertStringToModel(String input, Lang dataSourceFormat) {
		return RDFParserBuilder.create()
				.fromString(input)
				.lang(dataSourceFormat)
				.toModel();
	}

	public static String convertModelToString(Model model, Lang dataDestinationFormat) {
		StringWriter out = new StringWriter();

		RDFDataMgr.write(out, model, dataDestinationFormat);

		return out.toString();
	}
}
