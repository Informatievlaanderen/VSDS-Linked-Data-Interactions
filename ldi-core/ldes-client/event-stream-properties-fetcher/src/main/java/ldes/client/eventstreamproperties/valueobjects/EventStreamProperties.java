package ldes.client.eventstreamproperties.valueobjects;

import java.util.Optional;

public final class EventStreamProperties {
	private final String uri;
	private final String versionOfPath;
	private final String timestampPath;

	public EventStreamProperties(String uri) {
		this.uri = uri;
		this.versionOfPath = null;
		this.timestampPath = null;
	}

	public EventStreamProperties(String uri, String versionOfPath, String timestampPath) {
		this.uri = uri;
		this.versionOfPath = versionOfPath;
		this.timestampPath = timestampPath;
	}

	public String getUri() {
		return uri;
	}

	public Optional<String> getVersionOfPath() {
		return Optional.ofNullable(versionOfPath);
	}

	public Optional<String> getTimestampPath() {
		return Optional.ofNullable(timestampPath);
	}

	public boolean isComplete() {
		return getTimestampPath().isPresent() && getVersionOfPath().isPresent();
	}
}
