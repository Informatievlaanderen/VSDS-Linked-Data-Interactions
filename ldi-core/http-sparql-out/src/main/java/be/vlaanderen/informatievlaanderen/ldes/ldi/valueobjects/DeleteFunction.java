package be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects;

import java.util.List;
import java.util.Optional;

public class DeleteFunction {
	private final String query;

	private DeleteFunction(String query) {
		this.query = query;
	}

	public Optional<String> createQueryForResources(List<String> subjects) {
		return Optional.ofNullable(query).map(q -> q.formatted(String.join("> <", subjects)));
	}

	public Optional<String> createQueryForResources(String... subjects) {
		return createQueryForResources(List.of(subjects));
	}

	public static DeleteFunction empty() {
		return new DeleteFunction(null);
	}

	public static DeleteFunction ofQuery(String query) {
		return new DeleteFunction(query);
	}
}
