package be.vlaanderen.informatievlaanderen.ldes.ldio.events;

public record PipelineDeletedEvent(String pipelineId, boolean keepState) {
}
