package be.vlaanderen.informatievlaanderen.ldes.ldio.events;

import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus;

public record PipelineStatusEvent(String pipelineId, PipelineStatus status) {
}
