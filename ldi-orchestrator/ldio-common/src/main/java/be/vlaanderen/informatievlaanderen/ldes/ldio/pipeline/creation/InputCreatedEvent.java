package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation;

import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.components.LdioInput;

public record InputCreatedEvent(String pipelineName, LdioInput ldioInput) {
}
