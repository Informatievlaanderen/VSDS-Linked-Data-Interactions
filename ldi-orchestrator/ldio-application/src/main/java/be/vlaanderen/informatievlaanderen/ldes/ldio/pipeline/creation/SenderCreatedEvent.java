package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation;

import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.components.LdioSender;

public record SenderCreatedEvent(String pipelineName, LdioSender ldioSender) {
}
