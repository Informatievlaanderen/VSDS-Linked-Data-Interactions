package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

public record JmsConfig(String username, String password, String remoteUrl, String queue) {
}
