package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioChangeDetectionFilter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.status.LdioPipelineEventsListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LdioChangeDetectionEventsListenerConfig {
	@Bean
	public LdioPipelineEventsListener<LdioChangeDetectionFilter> ldioPipelineEventsListener() {
		return new LdioPipelineEventsListener.Builder<LdioChangeDetectionFilter>()
				.withStopBehavior(LdioChangeDetectionFilter::shutdown)
				.build();
	}
}
