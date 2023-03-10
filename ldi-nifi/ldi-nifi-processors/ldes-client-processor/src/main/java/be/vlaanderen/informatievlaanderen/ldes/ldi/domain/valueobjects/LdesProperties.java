package be.vlaanderen.informatievlaanderen.ldes.ldi.domain.valueobjects;

public class LdesProperties {
	private final String timestampPath;
	private final String versionOfPath;
	private final String shape;

	public LdesProperties(String timestampPath, String versionOfPath, String shape) {
		this.timestampPath = timestampPath;
		this.versionOfPath = versionOfPath;
		this.shape = shape;
	}

	public String getTimestampPath() {
		return timestampPath;
	}

	public String getVersionOfPath() {
		return versionOfPath;
	}

	public String getShape() {
		return shape;
	}
}
