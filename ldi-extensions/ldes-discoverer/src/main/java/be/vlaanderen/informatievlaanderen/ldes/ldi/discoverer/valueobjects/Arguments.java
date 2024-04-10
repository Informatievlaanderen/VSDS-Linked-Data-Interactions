package be.vlaanderen.informatievlaanderen.ldes.ldi.discoverer.valueobjects;

import org.springframework.boot.ApplicationArguments;

import java.util.List;
import java.util.Optional;

public class Arguments {
	private final ApplicationArguments applicationArguments;

	public Arguments(ApplicationArguments applicationArguments) {
		this.applicationArguments = applicationArguments;
	}

	public boolean containsKey(String key) {
		return applicationArguments.containsOption(key);
	}

	public List<String> getArgumentValues(String key) {
		final List<String> values = applicationArguments.getOptionValues(key);
		return values == null ? List.of() : values;
	}

	public String getRequiredValue(String key) {
		return getArgumentValues(key).stream()
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Missing configuration for %s".formatted(key)));
	}

	public Optional<Integer> getInteger(String key) {
		return getArgumentValues(key).stream()
				.findFirst()
				.map(Integer::parseInt);
	}
}
