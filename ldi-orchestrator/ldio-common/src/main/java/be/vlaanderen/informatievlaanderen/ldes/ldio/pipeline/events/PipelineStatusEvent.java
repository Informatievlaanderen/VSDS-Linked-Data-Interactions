package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.events;

import be.vlaanderen.informatievlaanderen.ldes.ldio.status.PipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.status.StatusChangeSource;

public record PipelineStatusEvent(String pipelineId, PipelineStatus status, StatusChangeSource statusChangeSource) {
}
