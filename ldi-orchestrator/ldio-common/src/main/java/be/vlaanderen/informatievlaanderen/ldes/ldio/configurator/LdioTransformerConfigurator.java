package be.vlaanderen.informatievlaanderen.ldes.ldio.configurator;

import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;

public interface LdioTransformerConfigurator {
	LdioTransformer configure(ComponentProperties properties);
}
