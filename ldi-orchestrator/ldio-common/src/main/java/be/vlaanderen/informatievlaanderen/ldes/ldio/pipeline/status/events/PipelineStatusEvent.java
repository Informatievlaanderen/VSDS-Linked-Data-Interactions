package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.status.events;

import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.status.PipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.status.StatusChangeSource;

public record PipelineStatusEvent(String pipelineId, PipelineStatus status, StatusChangeSource statusChangeSource) {
}
