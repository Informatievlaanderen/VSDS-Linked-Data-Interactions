package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config;

import org.apache.nifi.processor.Relationship;

public class ChangeDetectionFilterRelationships {
	private ChangeDetectionFilterRelationships() {
	}

	public static final Relationship NEW_STATE_RECEIVED = new Relationship.Builder()
			.name("new state received")
			.description("State member has changed and gone successfully through the filter")
			.build();

	public static final Relationship IGNORED = new Relationship.Builder()
			.name("ignored")
			.description("The state of the member has not been changed and therefor the member is ignored")
			.build();
}
