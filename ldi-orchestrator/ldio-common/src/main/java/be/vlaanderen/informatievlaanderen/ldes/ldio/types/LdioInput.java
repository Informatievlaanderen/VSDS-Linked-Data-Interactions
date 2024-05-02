package be.vlaanderen.informatievlaanderen.ldes.ldio.types;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import org.apache.jena.rdf.model.Model;

import java.util.function.Supplier;

/**
 * Base class for the start of a LDIO workflow.
 * <p>
 * Depending on its implementation, the LDI Input will be able to transfer
 * either generated or received data
 * onto the LDIO pipeline
 */
public abstract class LdioInput implements LdioStatusComponent {
	private final ComponentExecutor executor;
	private final LdiAdapter adapter;
	private final LdioObserver ldioObserver;


	/**
	 * Creates a LdiInput with its Component Executor and LDI Adapter
	 *
	 * @param executor Instance of the Component Executor. Allows the LDI Input to pass
	 *                 data on the pipeline
	 * @param adapter  Instance of the LDI Adapter. Facilitates transforming the input
	 *                 data to a linked data model (RDF).
	 */
	protected LdioInput(ComponentExecutor executor, LdiAdapter adapter, LdioObserver ldioObserver) {
		this.executor = executor;
		this.adapter = adapter;
		this.ldioObserver = ldioObserver;
	}

	public void processInput(String content, String contentType) {
		processInput(LdiAdapter.Content.of(content, contentType));
	}

	public void processInput(LdiAdapter.Content content) {
		final Supplier<String> failedContentLogSupplier = () -> "Processing below message.%n%n###mime###%n%s%n###content###%n%s".formatted(content.mimeType(), content.content());
		ldioObserver.observe(() -> adapter.apply(content).forEach(this::processModel), "processInput", failedContentLogSupplier);
	}

	protected void processModel(Model model) {
		ldioObserver.increment();
		ldioObserver.observe(() -> executor.transformLinkedData(model), "processModel");
	}
}
