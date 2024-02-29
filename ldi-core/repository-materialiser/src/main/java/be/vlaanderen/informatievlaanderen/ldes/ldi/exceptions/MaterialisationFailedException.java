package be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;

import java.util.List;
import java.util.stream.Collectors;

public class MaterialisationFailedException extends RuntimeException {
	private final transient List<Model> uncommittedModels;
	public MaterialisationFailedException(Exception e, List<Model> uncommittedModels) {
		super(e);
		this.uncommittedModels = uncommittedModels;
	}

	@Override
	public String getMessage() {
		final String uncommittedMembers = uncommittedModels.stream()
				.map(model -> RDFWriter.source(model).lang(Lang.TURTLE).asString())
				.collect(Collectors.joining("\n"));
		return "The following members could not be materialised to the triples store%n%n%s".formatted(uncommittedMembers);
	}
}
