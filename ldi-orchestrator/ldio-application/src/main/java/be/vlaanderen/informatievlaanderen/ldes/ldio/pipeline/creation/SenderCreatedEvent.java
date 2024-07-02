package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation;

import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.model.LdioSender;

public record SenderCreatedEvent(String pipelineName, LdioSender ldioSender) {
}
