package ldes.client.treenodefetcher.domain.valueobjects;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class MutabilityStatus {
	private final boolean mutable;
	private final LocalDateTime earliestNextVisit;

	public MutabilityStatus(boolean mutable, LocalDateTime earliestNextVisit) {
		this.mutable = mutable;
		this.earliestNextVisit = earliestNextVisit;
	}

	public static MutabilityStatus ofHeader(List<String> cacheControlHeader) {
		boolean immutable = cacheControlHeader.stream().noneMatch(headerValue -> headerValue.contains("immutable"));
		long maxAge = cacheControlHeader
				.stream()
				.flatMap(header -> Arrays.stream(header.split(", ")))
				.filter(headerValue -> headerValue.startsWith("max-age"))
				.mapToLong(headerValue -> Long.parseLong(headerValue.split("=")[1])).findAny().orElse(60);

		return new MutabilityStatus(immutable, LocalDateTime.now().plusSeconds(maxAge));
	}

	public boolean isMutable() {
		return mutable;
	}

	public LocalDateTime getEarliestNextVisit() {
		return earliestNextVisit;
	}
}
