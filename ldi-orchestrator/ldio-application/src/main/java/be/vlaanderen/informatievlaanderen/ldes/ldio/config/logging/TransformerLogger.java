package be.vlaanderen.informatievlaanderen.ldes.ldio.config.logging;

import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioTransformer;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransformerLogger extends LdioTransformer {
	private final Logger log;
	private final ObservationRegistry observationRegistry;
	public TransformerLogger(LdioTransformer ldiTransformer, ObservationRegistry observationRegistry) {
		this.observationRegistry = observationRegistry;
		log = LoggerFactory.getLogger(ldiTransformer.getClass());
	}

	@Override
	public void apply(Model model) {
		Observation.createNotStarted(this.getClass().getSimpleName(), observationRegistry)
				.contextualName("accept")
				.observe(() -> {
					try {
						next(model);
					} catch (Exception e) {
						log.atError().log("ERROR - problem='{}', when='accept'", e.getMessage());
						throw e;
					}
				});
	}
}
