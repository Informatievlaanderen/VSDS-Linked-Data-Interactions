package be.vlaanderen.informatievlaanderen.ldes.ldio.event;

import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientConnectorApi;

public record LdesClientConnectorApiCreatedEvent(String pipelineName,
                                                 LdioLdesClientConnectorApi ldesClientConnectorApi) {
}
