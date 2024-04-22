package be.vlaanderen.informatievlaanderen.ldes.ldi.discoverer.valueobjects;

import org.springframework.boot.ApplicationArguments;

import java.util.List;
import java.util.Optional;

public class Arguments {
	private final ApplicationArguments applicationArguments;

	public Arguments(ApplicationArguments applicationArguments) {
		this.applicationArguments = applicationArguments;
	}

	public boolean containsFlag(String key) {
		return applicationArguments.containsOption(key);
	}

	public List<String> getArgumentValues(String key) {
		final List<String> values = applicationArguments.getOptionValues(key);
		return values == null ? List.of() : values;
	}

	public Optional<String> getValue(String key) {
		return getArgumentValues(key).stream().findFirst();
	}

	public String getRequiredValue(String key) {
		return getValue(key).orElseThrow(() -> new IllegalArgumentException("Missing configuration for %s".formatted(key)));
	}

	public Optional<Integer> getInteger(String key) {
		try {
			return getArgumentValues(key).stream()
					.findFirst()
					.map(Integer::parseInt);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid configuration for %s: unable to parse number %s".formatted(key, e.getMessage()), e);
		}
	}
}
