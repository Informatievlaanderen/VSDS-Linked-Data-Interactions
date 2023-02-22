package be.vlaanderen.informatievlaanderen.ldes.ldi.config;

import org.apache.nifi.processor.Relationship;

public class LdesProcessorRelationships {

	private LdesProcessorRelationships() {
	}

	public static final Relationship DATA_RELATIONSHIP = new Relationship.Builder().name("data")
			.description("Posts LDES members to the remote URL").build();
}
