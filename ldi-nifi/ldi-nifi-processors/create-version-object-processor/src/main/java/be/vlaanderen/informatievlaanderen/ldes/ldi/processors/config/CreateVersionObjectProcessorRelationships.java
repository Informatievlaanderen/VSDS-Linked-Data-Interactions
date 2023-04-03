package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config;

import org.apache.nifi.processor.Relationship;

public class CreateVersionObjectProcessorRelationships {

	private CreateVersionObjectProcessorRelationships() {
	}

	public static final Relationship DATA_RELATIONSHIP = new Relationship.Builder().name("data")
			.description("Posts LDES members to the remote URL").build();

	public static final Relationship DATA_UNPARSEABLE_RELATIONSHIP = new Relationship.Builder().name("unparseable")
			.description("Unparseable data").build();
}
