package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.configurator;

import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.components.LdioTransformer;

/**
 * Interface to manage the configuration of the {@link LdioTransformer}
 * in LDIO. Implementations will typically be declared as a bean in a "Ldio[OutputName]AutoConfig" class that will be
 * annotated as {@link org.springframework.context.annotation.Configuration}
 */
@FunctionalInterface
public interface LdioTransformerConfigurator extends LdioConfigurator {
	@Override
	LdioTransformer configure(ComponentProperties properties);
}
