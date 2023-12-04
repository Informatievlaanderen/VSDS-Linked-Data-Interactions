package be.vlaanderen.informatievlaanderen.ldes.ldio.config.logging;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComponentExecutorLogger implements ComponentExecutor {
	private final ComponentExecutor componentExecutor;
	private final ObservationRegistry observationRegistry;
	private final Logger log;

	public ComponentExecutorLogger(ComponentExecutor componentExecutor, ObservationRegistry observationRegistry) {
		this.componentExecutor = componentExecutor;
		this.observationRegistry = observationRegistry;
		log = LoggerFactory.getLogger(componentExecutor.getClass());
	}

	@Override
	public void transformLinkedData(Model linkedDataModel) {
		Observation.createNotStarted(this.getClass().getSimpleName(), observationRegistry)
				.contextualName("accept")
				.observe(() -> {
					try {
						componentExecutor.transformLinkedData(linkedDataModel);
					} catch (Exception e) {
						log.atError().log("ERROR - problem='{}', when='accept'", e.getMessage());
						throw e;
					}
				});
	}
}
