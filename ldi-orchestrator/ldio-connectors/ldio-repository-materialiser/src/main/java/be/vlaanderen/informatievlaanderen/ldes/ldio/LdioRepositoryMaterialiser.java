package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.Materialiser;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import java.io.StringWriter;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioRepositoryMaterialiserProperties.*;

public class LdioRepositoryMaterialiser implements LdiOutput {

	private Materialiser materialiser;

	public LdioRepositoryMaterialiser(ComponentProperties config) {
		this.materialiser = new Materialiser(config.getProperty(SPARQL_HOST), config.getProperty(REPOSITORY_ID),
				config.getOptionalProperty(NAMED_GRAPH).orElse(""));
	}

	@Override
	public void accept(Model model) {
		StringWriter writer = new StringWriter();
		RDFDataMgr.write(writer, model, Lang.NQUADS);
		materialiser.process(writer.toString());
	}
}
