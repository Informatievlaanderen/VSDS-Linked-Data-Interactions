package be.vlaanderen.informatievlaanderen.ldes.ldi.validators;

import org.apache.jena.riot.RDFLanguages;
import org.apache.nifi.components.ValidationContext;
import org.apache.nifi.components.ValidationResult;
import org.apache.nifi.components.Validator;

public class RDFLanguageValidator implements Validator {

	public RDFLanguageValidator() {
		// Used by Jena
	}

	@Override
	public ValidationResult validate(final String subject, final String input, final ValidationContext context) {
		return new ValidationResult.Builder().subject(subject).input(input)
				.valid(RDFLanguages.nameToLang(input) != null)
				.explanation(String.format(
						"'%s' is not a supported RDF language. Refer to org.apache.jena.riot.RDFLanguages for an overview of available data formats.",
						subject))
				.build();
	}
}
