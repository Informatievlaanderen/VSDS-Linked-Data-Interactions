package be.vlaanderen.informatievlaanderen.ldes.ldio.configurator;

import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioProcessor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;

public interface LdioProcessorConfigurator {
	LdioProcessor configure(ComponentProperties properties);
}
