package be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;

public class InsertFunction {
	private final String query;

	public InsertFunction(String query) {
		this.query = query;
	}

	public String createQuery(Model model) {
		final String triples = RDFWriter.source(model).lang(Lang.NQUADS).asString();
		return query.formatted(triples);
	}

}
