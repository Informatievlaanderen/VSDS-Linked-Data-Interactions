package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObserveConfiguration {
	public static final String ERROR_TEMPLATE = "ERROR - when='{}', problem='{}'";
	private static ObservationRegistry observationRegistry;


	public static ObservationRegistry getObservationRegistry() {
		if (observationRegistry == null) {
			observationRegistry = ObservationRegistry.create();
		}

		return observationRegistry;
	}

	@Bean
	public ObservationRegistry observationRegistry() {
		return ObservationRegistry.create();
	}
}
