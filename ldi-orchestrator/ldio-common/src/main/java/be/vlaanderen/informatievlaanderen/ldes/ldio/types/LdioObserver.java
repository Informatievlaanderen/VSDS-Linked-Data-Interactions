package be.vlaanderen.informatievlaanderen.ldes.ldio.types;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.ObserveConfiguration;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.function.Supplier;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig.PIPELINE_NAME;

public class LdioObserver {
	private static final String LDIO_DATA_IN = "ldio_data_in";
	private static final String LDIO_COMPONENT_NAME = "ldio_type";
	private static final Logger log = LoggerFactory.getLogger(LdioObserver.class);


	private final String componentName;
	private final String pipelineName;
	private final ObservationRegistry observationRegistry;

	private LdioObserver(String componentName, String pipelineName, ObservationRegistry observationRegistry) {
		this.componentName = componentName;
		this.pipelineName = pipelineName;
		this.observationRegistry = observationRegistry;
	}

	@SafeVarargs
	public final void observe(Runnable observable, String location, Supplier<String>... additionalLoggingContent) {
		final String errorLocation = pipelineName + ":" + location;
		Observation.createNotStarted(this.componentName, observationRegistry)
				.observe(() -> {
					try {
						observable.run();
					} catch (Exception e) {
						log.atError().log(ObserveConfiguration.ERROR_TEMPLATE, errorLocation, e.getMessage());
						Arrays.stream(additionalLoggingContent)
								.forEach(content ->
										log.atError().log(ObserveConfiguration.ERROR_TEMPLATE, errorLocation, content.get())
								);
						throw e;
					}
				});
	}

	public void increment() {
		Metrics.counter(LDIO_DATA_IN, PIPELINE_NAME, pipelineName, LDIO_COMPONENT_NAME, componentName).increment();
	}

	public static LdioObserver register(String componentName, String pipelineName, ObservationRegistry observationRegistry) {
		Metrics.counter(LDIO_DATA_IN, PIPELINE_NAME, pipelineName, LDIO_COMPONENT_NAME, componentName).increment(0);
		return new LdioObserver(componentName, pipelineName, observationRegistry);
	}

	public String getPipelineName() {
		return pipelineName;
	}


}
