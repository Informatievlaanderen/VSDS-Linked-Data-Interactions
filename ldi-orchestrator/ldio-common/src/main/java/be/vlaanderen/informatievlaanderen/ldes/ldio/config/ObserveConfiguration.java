package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import io.micrometer.observation.ObservationRegistry;

public class ObserveConfiguration {
	public static final String ERROR_TEMPLATE = "ERROR - when='{}', problem='{}'";
	private static ObservationRegistry observationRegistry;

	private ObserveConfiguration() {}

	public static ObservationRegistry observationRegistry() {
		if (observationRegistry == null) {
			observationRegistry = ObservationRegistry.create();
		}

		return observationRegistry;
	}
}
