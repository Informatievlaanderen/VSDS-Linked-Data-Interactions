package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;

import java.util.concurrent.atomic.AtomicInteger;

public class LdioNoopOut implements LdiOutput {
	public static final String NAME = "Ldio:NoopOut";

	AtomicInteger i = new AtomicInteger();

	@Override
	public void accept(Model linkedDataModel) {
		String string = RDFWriter.source(linkedDataModel).lang(Lang.TURTLE).asString();
		System.out.println(string);
		// No Operation
	}
}
