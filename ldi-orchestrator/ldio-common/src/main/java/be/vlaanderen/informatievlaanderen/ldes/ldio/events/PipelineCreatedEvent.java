package be.vlaanderen.informatievlaanderen.ldes.ldio.events;

import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.PipelineStatusManager;

public record PipelineCreatedEvent(PipelineStatusManager pipelineStatusManager) {
	public String pipelineName() {
		return pipelineStatusManager.getPipelineName();
	}
}
