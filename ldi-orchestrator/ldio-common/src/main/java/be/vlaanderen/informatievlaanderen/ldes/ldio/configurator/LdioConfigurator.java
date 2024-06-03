package be.vlaanderen.informatievlaanderen.ldes.ldio.configurator;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;

/**
 * Base interface to configure all LDIO components except for the {@link be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput}
 */
@FunctionalInterface
public interface LdioConfigurator {
	/**
	 * Configures an LdiComponent based on the provided ComponentProperties
	 */
	LdiComponent configure(ComponentProperties properties);
}
