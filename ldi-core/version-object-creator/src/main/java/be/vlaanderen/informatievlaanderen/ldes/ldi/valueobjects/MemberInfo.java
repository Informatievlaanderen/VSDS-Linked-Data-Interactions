package be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects;

/**
 * Contains the information of a version object member
 */
public class MemberInfo {
	private final String versionOf;
	private final String observedAt;

	public MemberInfo(String versionOf, String observedAt) {
		this.versionOf = versionOf;
		this.observedAt = observedAt;
	}

	public String getVersionOf() {
		return versionOf;
	}

	public String getObservedAt() {
		return observedAt;
	}

	public String generateVersionObjectId(String delimiter) {
		return versionOf + delimiter + observedAt;
	}
}
