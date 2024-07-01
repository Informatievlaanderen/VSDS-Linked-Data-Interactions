package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.configurator;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.components.LdioInput;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Interface to manage the configuration of the {@link LdioInput}
 * in LDIO. Implementations will typically be declared as a bean in a "Ldio[OutputName]AutoConfig" class that will be
 * annotated as {@link org.springframework.context.annotation.Configuration}
 */
public interface LdioInputConfigurator {
	LdioInput configure(LdiAdapter adapter, ComponentExecutor executor, ApplicationEventPublisher eventPublisher, ComponentProperties properties);

	boolean isAdapterRequired();
}
