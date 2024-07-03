package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.ChangeDetectionFilter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.HashedStateMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioChangeDetectionFilter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioTransformerConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.persistence.PersistenceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioChangeDetectionFilter.NAME;

@Configuration
public class LdioChangeDetectionFilterAutoConfig {

	@SuppressWarnings("java:S6830")
	@Bean(NAME)
	public LdioTransformerConfigurator ldioChangeDetectionFilterConfigurator() {
		return new LdioChangeDetectionFilterConfigurator();
	}

	public static class LdioChangeDetectionFilterConfigurator implements LdioTransformerConfigurator {
		@Override
		public LdioTransformer configure(ComponentProperties properties) {
			final HashedStateMemberRepository repository = new HasedStateMemberRepositoryFactory(properties).getHashedStateMemberRepository();
			final boolean keepState = properties.getOptionalBoolean(PersistenceProperties.KEEP_STATE).orElse(false);
			final ChangeDetectionFilter changeDetectionFilter = new ChangeDetectionFilter(repository, keepState);
			return new LdioChangeDetectionFilter(changeDetectionFilter);
		}
	}
}
