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
	private final ComponentExecutor executor;
	private final LdiAdapter adapter;
	private final LdioObserver ldioObserver;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private PipelineStatus pipelineStatus;

	/**
	 * Creates a LdiInput with its Component Executor and LDI Adapter
	 *
	 * @param executor Instance of the Component Executor. Allows the LDI Input to pass
	 *                 data on the pipeline
	 * @param adapter  Instance of the LDI Adapter. Facilitates transforming the input
	 *                 data to a linked data model (RDF).
	 */
	protected LdioInput(ComponentExecutor executor, LdiAdapter adapter,
						LdioObserver ldioObserver, ApplicationEventPublisher applicationEventPublisher) {
		this.executor = executor;
		this.adapter = adapter;
		this.ldioObserver = ldioObserver;
		this.applicationEventPublisher = applicationEventPublisher;
		this.pipelineStatus = INIT;
	}

	public void processInput(String content, String contentType) {
		processInput(LdiAdapter.Content.of(content, contentType));
	}

	public void processInput(LdiAdapter.Content content) {
		ldioObserver.observe(() -> adapter.apply(content).forEach(this::processModel), "processInput", () -> "Processing below message.%n%n###mime###%n%s%n###content###%n%s".formatted(content.mimeType(), content.content()));
	}

	protected void processModel(Model model) {
		ldioObserver.increment();
		ldioObserver.observe(() -> executor.transformLinkedData(model), "processModel");
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
			// TODO: fix pipelineName
			default -> log.warn("Unhandled status update on pipeline: {} for status: {}", "pipelineName", pipelineStatus);
		}

		applicationEventPublisher.publishEvent(new PipelineStatusEvent("pipelineName", this.pipelineStatus, MANUAL));
		return this.pipelineStatus;
	}

	protected abstract void resume();

	protected abstract void pause();

	public void start() {
		updateStatus(START);
	}

	public PipelineStatus getStatus() {
		return this.pipelineStatus;
	}
}
