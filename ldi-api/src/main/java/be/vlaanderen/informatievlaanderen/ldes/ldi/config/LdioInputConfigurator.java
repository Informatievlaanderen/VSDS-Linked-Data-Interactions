package be.vlaanderen.informatievlaanderen.ldes.ldi.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;

public interface LdioInputConfigurator {
	Object configure(LdiAdapter adapter, ComponentExecutor executor,ComponentProperties properties);
}
