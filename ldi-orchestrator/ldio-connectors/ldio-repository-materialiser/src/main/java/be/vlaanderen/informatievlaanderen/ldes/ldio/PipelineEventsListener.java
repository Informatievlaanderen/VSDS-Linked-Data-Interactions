package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineStatusEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class PipelineEventsListener {
	private final Map<String, LdioRepositoryMaterialiser> materialisers = new HashMap<>();

	public void registerMaterialiser(String pipelineName, LdioRepositoryMaterialiser materialiser) {
		Objects.requireNonNull(pipelineName);
		materialisers.put(pipelineName, materialiser);
	}

	@EventListener
	public void handlePipelineDeletedEvent(PipelineDeletedEvent event) {
		LdioRepositoryMaterialiser materialiser = materialisers.remove(event.pipelineId());
		if (materialiser != null) {
			materialiser.shutdown();
		}
	}

	@EventListener(condition = "#event.status().name() == 'HALTED'")
	public void handlePipelineHaltedEvent(PipelineStatusEvent event) {
		final LdioRepositoryMaterialiser materialiser = materialisers.get(event.pipelineId());
		if(materialiser != null) {
			materialiser.sendToMaterialiser();
			materialiser.shutdown();
		}
	}

	@EventListener(condition = "#event.status().name() == 'RUNNING'")
	public void handlePipelineRunningEvent(PipelineStatusEvent event) {
		final LdioRepositoryMaterialiser materialiser = materialisers.get(event.pipelineId());
		if(materialiser != null) {
			materialiser.start();
		}
	}

}
