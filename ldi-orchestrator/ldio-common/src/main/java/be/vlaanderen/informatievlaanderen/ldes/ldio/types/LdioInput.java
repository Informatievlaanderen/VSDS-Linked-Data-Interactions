package be.vlaanderen.informatievlaanderen.ldes.ldio.types;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.ObserveConfiguration;
import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineStatusEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatusTrigger;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig.PIPELINE_NAME;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatusTrigger.START;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.StatusChangeSource.MANUAL;

/**
 * Base class for the start of a LDIO workflow.
 * <p>
 * Depending on its implementation, the LDI Input will be able to transfer
 * either generated or received data
 * onto the LDIO pipeline
 */
public abstract class LdioInput implements LdiComponent {

	protected final String componentName;
	protected final String pipelineName;
	private final ComponentExecutor executor;
	private final LdiAdapter adapter;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private final ObservationRegistry observationRegistry;

	private static final String LDIO_DATA_IN = "ldio_data_in";
	private static final String LDIO_COMPONENT_NAME = "ldio_type";
	private PipelineStatus pipelineStatus;

	/**
	 * Creates a LdiInput with its Component Executor and LDI Adapter
	 *
	 * @param executor Instance of the Component Executor. Allows the LDI Input to pass
	 *                 data on the pipeline
	 * @param adapter  Instance of the LDI Adapter. Facilitates transforming the input
	 *                 data to a linked data model (RDF).
	 */
	protected LdioInput(String componentName, String pipelineName, ComponentExecutor executor, LdiAdapter adapter,
						ObservationRegistry observationRegistry, ApplicationEventPublisher applicationEventPublisher) {
		this.componentName = componentName;
		this.pipelineName = pipelineName;
		this.executor = executor;
		this.adapter = adapter;
		this.observationRegistry = observationRegistry;
		this.applicationEventPublisher = applicationEventPublisher;
		this.pipelineStatus = INIT;
		Metrics.counter(LDIO_DATA_IN, PIPELINE_NAME, pipelineName, LDIO_COMPONENT_NAME, componentName).increment(0);
	}

	public void processInput(String content, String contentType) {
		processInput(LdiAdapter.Content.of(content, contentType));
	}

	public void processInput(LdiAdapter.Content content) {
		Observation.createNotStarted(this.componentName, observationRegistry)
				.observe(() -> {
					try {
						adapter.apply(content).forEach(this::processModel);
					} catch (Exception e) {
						final var errorLocation = this.pipelineName + ":processInput";
						log.atError().log(ObserveConfiguration.ERROR_TEMPLATE, errorLocation, e.getMessage());
						log.atError().log(ObserveConfiguration.ERROR_TEMPLATE, errorLocation,
								"Processing below message.%n%n###mime###%n%s%n###content###%n%s".formatted(content.mimeType(), content.content()));
						throw e;
					}
				});
	}

	protected void processModel(Model model) {
		Metrics.counter(LDIO_DATA_IN, PIPELINE_NAME, pipelineName, LDIO_COMPONENT_NAME, componentName).increment();
		Observation.createNotStarted(this.componentName, observationRegistry)
				.observe(() -> {
					try {
						executor.transformLinkedData(model);
					} catch (Exception e) {
						log.atError().log(ObserveConfiguration.ERROR_TEMPLATE, this.pipelineName + ":processModel", e.getMessage());
						throw e;
					}
				});
	}

	public abstract void shutdown();

	public PipelineStatus updateStatus(PipelineStatusTrigger trigger) {
		switch (trigger) {
			case START -> this.pipelineStatus = RUNNING;
			case RESUME -> {
				this.resume();
				this.pipelineStatus = RUNNING;
			}
			case HALT -> {
				if (this.pipelineStatus != INIT) {
					this.pause();
					this.pipelineStatus = HALTED;
				}
			}
			case STOP -> this.pipelineStatus = STOPPED;
			default -> log.warn("Unhandled status update on pipeline: {} for status: {}", pipelineName, pipelineStatus);
		}

		applicationEventPublisher.publishEvent(new PipelineStatusEvent(pipelineName, this.pipelineStatus, MANUAL));
		return this.pipelineStatus;
	}

	protected abstract void resume();

	protected abstract void pause();

	public void starting() {
		updateStatus(START);
	}

	public PipelineStatus getStatus() {
		return this.pipelineStatus;
	}
}
