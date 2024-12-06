package ldes.client.eventstreamproperties.valueobjects;

public final class EventStreamProperties {
	private final String uri;
	private final String versionOfPath;
	private final String timestampPath;
	private final String shaclShapeUri;

	public EventStreamProperties(String uri) {
		this(uri, null, null, null);
	}

	public EventStreamProperties(String uri, String versionOfPath, String timestampPath, String shaclShapeUri) {
		this.uri = uri;
		this.versionOfPath = versionOfPath;
		this.timestampPath = timestampPath;
		this.shaclShapeUri = shaclShapeUri;
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

	public String getShaclShapeUri() {
		return shaclShapeUri;
	}

	public boolean containsRequiredProperties() {
		return timestampPath != null && versionOfPath != null;
	}
}
