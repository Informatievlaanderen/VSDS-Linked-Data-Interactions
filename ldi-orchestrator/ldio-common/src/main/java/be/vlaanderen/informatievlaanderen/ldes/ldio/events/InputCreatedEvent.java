package be.vlaanderen.informatievlaanderen.ldes.ldio.events;

import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;

public record InputCreatedEvent(String pipelineName, LdioInput ldioInput) {
}
