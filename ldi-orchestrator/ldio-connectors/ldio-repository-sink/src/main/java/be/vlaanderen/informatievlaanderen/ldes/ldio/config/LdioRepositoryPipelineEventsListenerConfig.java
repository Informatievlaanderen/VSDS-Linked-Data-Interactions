package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioRepositorySink;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.status.LdioPipelineEventsListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LdioRepositoryPipelineEventsListenerConfig {
	@Bean
	public LdioPipelineEventsListener<LdioRepositorySink> ldioRepositoryMaterialiserLdioPipelineEventsListener() {
		return new LdioPipelineEventsListener.Builder<LdioRepositorySink>()
				.withStartBehavior(LdioRepositorySink::start)
				.withResumeBehavior(LdioRepositorySink::start)
				.withStopBehavior(LdioRepositorySink::shutdown)
				.withPauseBehavior(materialiser -> {
					materialiser.sendToSink();
					materialiser.shutdown();
				})
				.build();
	}
}
