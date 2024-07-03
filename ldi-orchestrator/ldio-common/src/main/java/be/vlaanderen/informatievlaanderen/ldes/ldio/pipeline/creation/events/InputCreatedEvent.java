package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.events;

import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioInput;

public record InputCreatedEvent(String pipelineName, LdioInput ldioInput) {
}
