package be.vlaanderen.informatievlaanderen.ldes.client.valueobjects;

public enum LdesProcessingState {

	CREATED("STATE: CREATED"), QUEUED("STATE: QUEUED"), PROCESSING("STATE: PROCESSING"), PROCESSED_IMMUTABLE(
			"STATE: PROCESSED IMMUTABLE"), PROCESSED_MUTABLE("STATE: PROCESSED MUTABLE");

	private final String description;

	private LdesProcessingState(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
