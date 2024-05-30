package ldes.client.treenodefetcher.domain.valueobjects;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Keeps track when the received http response is about the change, is in fact a reflection of the
 * <code>Cache-Control</code> header
 */
public class MutabilityStatus {

	public static final int DEFAULT_MAX_AGE = 60;

	private final boolean mutable;
	private final LocalDateTime earliestNextVisit;

	public MutabilityStatus(boolean mutable, LocalDateTime earliestNextVisit) {
		this.mutable = mutable;
		this.earliestNextVisit = earliestNextVisit;
	}

	public static MutabilityStatus empty() {
		return new MutabilityStatus(true, LocalDateTime.now().plusSeconds(DEFAULT_MAX_AGE));
	}

	public static MutabilityStatus ofHeader(String cacheControlHeader) {
		boolean mutable = !cacheControlHeader.contains("immutable");
		long maxAge = Arrays.stream(cacheControlHeader.split(", "))
				.filter(headerValue -> headerValue.startsWith("max-age"))
				.mapToLong(headerValue -> Long.parseLong(headerValue.split("=")[1]))
				.findAny()
				.orElse(DEFAULT_MAX_AGE);

		return new MutabilityStatus(mutable, LocalDateTime.now().plusSeconds(maxAge));
	}

	public boolean isMutable() {
		return mutable;
	}

	public LocalDateTime getEarliestNextVisit() {
		return earliestNextVisit;
	}
}
