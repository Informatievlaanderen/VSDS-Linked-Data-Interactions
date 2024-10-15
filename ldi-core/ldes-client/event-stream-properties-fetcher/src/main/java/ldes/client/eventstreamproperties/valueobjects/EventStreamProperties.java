package ldes.client.eventstreamproperties.valueobjects;

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

	public String getVersionOfPath() {
		return versionOfPath;
	}

	public String getTimestampPath() {
		return timestampPath;
	}

	public boolean containsRequiredProperties() {
		return timestampPath != null && versionOfPath != null;
	}
}
