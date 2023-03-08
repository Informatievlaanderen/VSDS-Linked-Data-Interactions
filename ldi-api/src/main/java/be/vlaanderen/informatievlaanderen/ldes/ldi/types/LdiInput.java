package be.vlaanderen.informatievlaanderen.ldes.ldi.types;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;

/**
 * Base class for the start of a LDIO workflow.
 *
 * Depending on its implementation, the LDI Input will be able to transfer
 * either generated or received data
 * onto the LDIO pipeline
 */
public abstract class LdiInput implements LdiComponent {

	private final ComponentExecutor executor;
	private final LdiAdapter adapter;

	/**
	 * Creates a LdiInput with its Component Executor and LDI Adapter
	 *
	 * @param executor
	 *            Instance of the Component Executor. Allows the LDI Input to pass
	 *            data on the pipeline
	 * @param adapter
	 *            Instance of the LDI Adapter. Facilitates transforming the input
	 *            data to a linked data model (RDF).
	 */
	protected LdiInput(ComponentExecutor executor, LdiAdapter adapter) {
		this.executor = executor;
		this.adapter = adapter;
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
