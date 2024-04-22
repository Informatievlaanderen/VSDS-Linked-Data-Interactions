package be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.JmsConfig;

public record LdioAmpqInProperties(String pipelineName, String defaultContentType, JmsConfig jmsConfig) {
}
