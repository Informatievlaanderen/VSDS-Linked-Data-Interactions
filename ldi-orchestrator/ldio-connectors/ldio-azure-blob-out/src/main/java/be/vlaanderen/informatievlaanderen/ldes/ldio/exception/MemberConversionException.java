package be.vlaanderen.informatievlaanderen.ldes.ldio.exception;

import be.vlaanderen.informatievlaanderen.ldes.ldio.util.ModelConverter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;

public class MemberConversionException extends RuntimeException {
	private final Model model;

	public MemberConversionException(Model model, Throwable ex) {
		super(ex);
		this.model = model;
	}

	@Override
	public String getMessage() {
		return "Could not convert Model:\n" + ModelConverter.toString(model, Lang.TURTLE);
	}
}
