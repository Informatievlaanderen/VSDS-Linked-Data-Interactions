package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.HttpSparqlOut;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import org.apache.jena.rdf.model.Model;

public class LdioHttpSparqlOut implements LdiOutput {
	public static final String NAME = "Ldio:HttpSparqlOut";
	private final HttpSparqlOut httpSparqlOut;

	public LdioHttpSparqlOut(HttpSparqlOut httpSparqlOut) {
		this.httpSparqlOut = httpSparqlOut;
	}

	@Override
	public void accept(Model model) {
		httpSparqlOut.write(model);
	}
}
