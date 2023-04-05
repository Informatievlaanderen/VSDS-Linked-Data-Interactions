package be.vlaanderen.informatievlaanderen.ldes.ldio.exception;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;

public class LdiAdapterMissingException extends RuntimeException {
	private final String pipelineName;

	public LdiAdapterMissingException(String pipelineName) {
		this.pipelineName = pipelineName;
	}

	@Override
	public String getMessage() {
		return "Missing LDI Adapter for pipeline " + pipelineName;
	}

	/**
	 * Validates if LDI Adapter is not null
	 *
	 * @param pipelineName
	 *            Name of the LDI pipeline
	 * @param adapter
	 *            The LDI adapter for the pipeline
	 * @throws LdiAdapterMissingException
	 *             When adapter is null
	 */
	public static void verifyAdapterPresent(String pipelineName, LdiAdapter adapter) {
		if (adapter == null) {
			throw new LdiAdapterMissingException(pipelineName);
		}
	}
}
