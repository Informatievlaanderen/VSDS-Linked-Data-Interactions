package be.vlaanderen.informatievlaanderen.ldes.ldi.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;

public interface LdioInputConfigurator {
	LdiComponent configure(LdiAdapter adapter, ComponentExecutor executor,ComponentProperties properties);
}
