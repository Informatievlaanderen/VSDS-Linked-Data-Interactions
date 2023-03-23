package be.vlaanderen.informatievlaanderen.ldes.ldio.events;

import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus;

public class PipelineStatusEvent {
	private final PipelineStatus status;

	public PipelineStatusEvent(PipelineStatus status) {
		this.status = status;
	}

	public PipelineStatus getStatus() {
		return status;
	}
}
