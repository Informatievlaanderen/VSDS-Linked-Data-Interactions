package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import io.micrometer.observation.ObservationRegistry;

public class LdioHttpInProcess extends LdioInput {
	public static final String NAME = "be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpIn";

	public LdioHttpInProcess(String pipelineName, ComponentExecutor executor, LdiAdapter adapter, ObservationRegistry observationRegistry) {
		super(NAME, pipelineName, executor, adapter, observationRegistry);
	}
}
