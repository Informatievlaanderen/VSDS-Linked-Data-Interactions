package be.vlaanderen.informatievlaanderen.ldes.ldio.types;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import io.micrometer.core.instrument.Metrics;
import org.apache.jena.rdf.model.Model;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioMetricValues.*;

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

	/**
	 * Creates a LdiInput with its Component Executor and LDI Adapter
	 *
	 * @param executor Instance of the Component Executor. Allows the LDI Input to pass
	 *                 data on the pipeline
	 * @param adapter  Instance of the LDI Adapter. Facilitates transforming the input
	 *                 data to a linked data model (RDF).
	 */
	protected LdioInput(String componentName, String pipelineName, ComponentExecutor executor, LdiAdapter adapter) {
		this.componentName = componentName;
		this.pipelineName = pipelineName;
		this.executor = executor;
		this.adapter = adapter;
		Metrics.counter(LDIO_DATA_IN, PIPELINE, pipelineName, LDIO_COMPONENT_NAME, componentName).increment(0);
	}

	protected void processInput(String content, String contentType) {
		getAdapter().apply(LdiAdapter.Content.of(content, contentType))
				.forEach(this::processModel);
	}

	protected void processModel(Model model) {
		Metrics.counter(LDIO_DATA_IN, PIPELINE, pipelineName, LDIO_COMPONENT_NAME, componentName).increment();
		getExecutor().transformLinkedData(model);
	}

	/**
	 * @return An instance of the configured Component Executor
	 */
	public ComponentExecutor getExecutor() {
		return executor;
	}

	/**
	 * @return An instance of the configured LDI Adapter
	 */
	public LdiAdapter getAdapter() {
		return adapter;
	}
}
