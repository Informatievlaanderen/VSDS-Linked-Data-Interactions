package be.vlaanderen.informatievlaanderen.ldes.ldio.types;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.ObserveConfiguration;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig.PIPELINE_NAME;

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
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private final ObservationRegistry observationRegistry;

	private static final String LDIO_DATA_IN = "ldio_data_in";
	private static final String LDIO_COMPONENT_NAME = "ldio_type";

	/**
	 * Creates a LdiInput with its Component Executor and LDI Adapter
	 *
	 * @param executor Instance of the Component Executor. Allows the LDI Input to pass
	 *                 data on the pipeline
	 * @param adapter  Instance of the LDI Adapter. Facilitates transforming the input
	 *                 data to a linked data model (RDF).
	 */
	protected LdioInput(String componentName, String pipelineName, ComponentExecutor executor, LdiAdapter adapter, ObservationRegistry observationRegistry) {
		this.componentName = componentName;
		this.pipelineName = pipelineName;
		this.executor = executor;
		this.adapter = adapter;
		this.observationRegistry = observationRegistry;
		Metrics.counter(LDIO_DATA_IN, PIPELINE_NAME, pipelineName, LDIO_COMPONENT_NAME, componentName).increment(0);
	}

	public void processInput(String content, String contentType) {
		processInput(LdiAdapter.Content.of(content, contentType));
	}

	protected void processInput(LdiAdapter.Content content) {
		Observation.createNotStarted(this.componentName, observationRegistry)
				.observe(() -> {
					try {
						adapter.apply(content).forEach(this::processModel);
					} catch (Exception e) {
						log.atError().log(ObserveConfiguration.ERROR_TEMPLATE, this.pipelineName + ":processInput", e.getMessage());
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
}
