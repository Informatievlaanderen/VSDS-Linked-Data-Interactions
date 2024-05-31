package be.vlaanderen.informatievlaanderen.ldes.ldio.configurator;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;

/**
 * Interface to manage the configuration of the {@link LdiOutput}
 * in LDIO. Implementations will typically be declared as a bean in a "Ldio[OutputName]AutoConfig" class that will be
 * annotated as {@link org.springframework.context.annotation.Configuration}
 */
@FunctionalInterface
public interface LdioOutputConfigurator extends LdioConfigurator {
}
