package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.h2.H2EntityManager;
import be.vlaanderen.informatievlaanderen.ldes.ldi.h2.H2Properties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.HashedStateMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.sql.SqlHashedStateMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.ComponentProperties;

public class HasedStateMemberRepositoryFactory {
	private final ComponentProperties properties;

	public HasedStateMemberRepositoryFactory(ComponentProperties properties) {
		this.properties = properties;
	}

	public HashedStateMemberRepository getHashedStateMemberRepository() {

		var h2EntityManager = H2EntityManager.getInstance(properties.getPipelineName(), new H2Properties(properties.getPipelineName()).getProperties());
		return new SqlHashedStateMemberRepository(h2EntityManager, properties.getPipelineName());
	}
}
