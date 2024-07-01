package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.configurator;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.components.LdioInput;

/**
 * Base interface to configure all LDIO components except for the {@link LdioInput}
 */
@FunctionalInterface
public interface LdioConfigurator {
	/**
	 * Configures an LdiComponent based on the provided ComponentProperties
	 */
	LdiComponent configure(ComponentProperties properties);
}
