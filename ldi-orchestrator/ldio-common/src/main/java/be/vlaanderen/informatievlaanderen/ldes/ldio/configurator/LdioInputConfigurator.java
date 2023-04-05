package be.vlaanderen.informatievlaanderen.ldes.ldio.configurator;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;

public interface LdioInputConfigurator {
	Object configure(LdiAdapter adapter, ComponentExecutor executor, ComponentProperties properties);
}
