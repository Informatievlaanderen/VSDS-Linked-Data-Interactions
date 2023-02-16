package be.vlaanderen.informatievlaanderen.ldes.ldi.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;

import java.util.Map;

public interface LdioConfigurator {
	LdiComponent configure(Map<String, String> properties);
}
