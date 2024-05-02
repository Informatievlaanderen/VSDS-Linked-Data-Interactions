package be.vlaanderen.informatievlaanderen.ldes.ldio.events;

import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.PipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.StatusChangeSource;

public record PipelineStatusEvent(String pipelineId, PipelineStatus status, StatusChangeSource statusChangeSource) {
}
