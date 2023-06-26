package be.vlaanderen.informatievlaanderen.ldes.ldio.exception;

import be.vlaanderen.informatievlaanderen.ldes.ldio.util.ModelConverter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.util.MemberIdExtractor.TREE_MEMBER;

public class MemberIdNotFoundException extends RuntimeException {

	private final Model model;

	public MemberIdNotFoundException(Model model) {
		super();
		this.model = model;
	}

	@Override
	public String getMessage() {
		return "Could not extract " + TREE_MEMBER + " statement from " + ModelConverter.toString(model, Lang.TURTLE);
	}
}
