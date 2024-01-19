package be.vlaanderen.informatievlaanderen.ldes.ldio.event;

import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpInProcess;

public record HttpInPipelineCreatedEvent(String pipelineName, LdioHttpInProcess ldioHttpInProcess) {
}
