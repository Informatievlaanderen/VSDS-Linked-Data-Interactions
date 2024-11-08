package be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DeleteFunction {
	private final String query;

	private DeleteFunction(String query) {
		this.query = query;
	}

	public Optional<String> createQueryForResources(List<String> subjects) {
		final String joinedSubjects = subjects.stream()
				.map("<%s>"::formatted)
				.collect(Collectors.joining(","));
		return Optional.ofNullable(query).map(q -> q.formatted(joinedSubjects));
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
