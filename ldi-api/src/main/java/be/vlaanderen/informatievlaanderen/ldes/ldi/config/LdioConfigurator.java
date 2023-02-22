package be.vlaanderen.informatievlaanderen.ldes.ldi.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;

public interface LdioConfigurator {
	LdiComponent configure(ComponentProperties properties);
}
