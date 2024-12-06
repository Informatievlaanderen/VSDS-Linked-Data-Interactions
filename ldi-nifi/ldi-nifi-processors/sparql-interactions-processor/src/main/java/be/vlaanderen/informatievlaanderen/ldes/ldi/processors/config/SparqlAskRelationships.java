package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config;

import org.apache.nifi.processor.Relationship;

public class SparqlAskRelationships {
	private SparqlAskRelationships() {
	}

	public static final Relationship TRUE = new Relationship.Builder().name(Boolean.TRUE.toString()).build();
	public static final Relationship FALSE = new Relationship.Builder().name(Boolean.FALSE.toString()).build();
}
