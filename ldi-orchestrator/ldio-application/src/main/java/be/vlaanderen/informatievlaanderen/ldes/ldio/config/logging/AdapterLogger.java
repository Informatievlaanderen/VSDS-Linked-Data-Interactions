package be.vlaanderen.informatievlaanderen.ldes.ldio.config.logging;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class AdapterLogger implements LdiAdapter {
	private final Logger log;
	private final LdiAdapter adapter;
	private final ObservationRegistry observationRegistry;

	public AdapterLogger(LdiAdapter adapter, ObservationRegistry observationRegistry) {
		this.adapter = adapter;
		this.observationRegistry = observationRegistry;
		log = LoggerFactory.getLogger(adapter.getClass());
	}


	@Override
	public Stream<Model> apply(Content content) {
		AtomicReference<Stream<Model>> stream = new AtomicReference<>();
		Observation.createNotStarted(this.getClass().getSimpleName(), observationRegistry)
				.contextualName("accept")
				.observe(() -> {
					try {
						stream.set(adapter.apply(content));
					} catch (Exception e) {
						log.atError().log("ERROR - problem='{}', when='accept'", e.getMessage());
						throw e;
					}
				});
		return stream.get();
	}
}
