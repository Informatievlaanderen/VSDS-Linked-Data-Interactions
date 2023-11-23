package be.vlaanderen.informatievlaanderen.ldes.ldio.types;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import io.micrometer.core.instrument.Metrics;
import org.apache.jena.rdf.model.Model;

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
	protected LdioInput(String componentName, String pipelineName, ComponentExecutor executor, LdiAdapter adapter) {
		this.componentName = componentName;
		this.pipelineName = pipelineName;
		this.executor = executor;
		this.adapter = adapter;
		Metrics.counter(LDIO_DATA_IN, PIPELINE_NAME, pipelineName, LDIO_COMPONENT_NAME, componentName).increment(0);
	}

	protected void processInput(String content, String contentType) {
		processInput(LdiAdapter.Content.of(content, contentType));
	}

	protected void processInput(LdiAdapter.Content content) {
		adapter.apply(content).forEach(this::processModel);
	}

	protected void processModel(Model model) {
		Metrics.counter(LDIO_DATA_IN, PIPELINE_NAME, pipelineName, LDIO_COMPONENT_NAME, componentName).increment();
		executor.transformLinkedData(model);
	}
}
