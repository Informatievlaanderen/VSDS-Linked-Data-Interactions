package be.vlaanderen.informatievlaanderen.ldes.ldio.config.logging;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdiOutputLogger implements LdiOutput {
	private final LdiOutput ldiOutput;
	private final ObservationRegistry observationRegistry;
	private final Logger log;

	public LdiOutputLogger(LdiOutput ldiOutput, ObservationRegistry observationRegistry) {
		this.ldiOutput = ldiOutput;
		this.observationRegistry = observationRegistry;
		log = LoggerFactory.getLogger(ldiOutput.getClass());
	}

	@Override
	public void accept(Model model) {
		Observation.createNotStarted(this.getClass().getSimpleName(), observationRegistry)
				.contextualName("accept")
				.observe(() -> {
					try {
						ldiOutput.accept(model);
					} catch (Exception e) {
						log.atError().log("ERROR - problem='{}', when='accept'", e.getMessage());
						throw e;
					}
				});
	}
}
