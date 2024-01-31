package be.vlaanderen.informatievlaanderen.ldes.ldio.events;

import be.vlaanderen.informatievlaanderen.ldes.ldio.components.LdioSender;

public record SenderCreatedEvent(String pipelineName, LdioSender ldioSender) {
}
