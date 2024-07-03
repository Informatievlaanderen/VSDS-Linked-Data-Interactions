package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioRepositoryMaterialiser;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.status.LdioPipelineEventsListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LdioRepositoryPipelineEventsListenerConfig {
	@Bean
	public LdioPipelineEventsListener<LdioRepositoryMaterialiser> ldioRepositoryMaterialiserLdioPipelineEventsListener() {
		return new LdioPipelineEventsListener.Builder<LdioRepositoryMaterialiser>()
				.withStartBehavior(LdioRepositoryMaterialiser::start)
				.withResumeBehavior(LdioRepositoryMaterialiser::start)
				.withStopBehavior(LdioRepositoryMaterialiser::shutdown)
				.withPauseBehavior(materialiser -> {
					materialiser.sendToMaterialiser();
					materialiser.shutdown();
				})
				.build();
	}
}
