package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config;

import org.apache.nifi.components.ValidationContext;
import org.apache.nifi.components.ValidationResult;
import org.apache.nifi.components.Validator;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Objects;

public class LdesUrlValidator implements Validator {
	@Override
	public ValidationResult validate(final String subject, final String input, final ValidationContext context) {
		if (context.isExpressionLanguageSupported(subject) && context.isExpressionLanguagePresent(input)) {
			return new ValidationResult.Builder().subject(subject).input(input).explanation("Expression Language Present").valid(true).build();
		}

		var invalidURIs = Arrays.stream(input.split(","))
				.map(string -> {
					try {
						new URI(input);
						return null;
					} catch (URISyntaxException e) {
						return new RuntimeException(e);
					}
				})
				.filter(Objects::nonNull)
				.count();

		if (invalidURIs == 0) {
			return new ValidationResult.Builder().subject(subject).input(input).explanation("Valid URI(s)").valid(true).build();
		} else {
			return new ValidationResult.Builder().subject(subject).input(input).explanation("Not (a) valid URI(s)").valid(false).build();
		}
	}
}
