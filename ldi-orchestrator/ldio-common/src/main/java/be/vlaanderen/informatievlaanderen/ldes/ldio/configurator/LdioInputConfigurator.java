package be.vlaanderen.informatievlaanderen.ldes.ldio.configurator;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.PipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.springframework.context.ApplicationEventPublisher;

public interface LdioInputConfigurator {
	LdioInput configure(LdiAdapter adapter, ComponentExecutor executor, ApplicationEventPublisher eventPublisher, ComponentProperties properties);

	boolean isAdapterRequired();
}
