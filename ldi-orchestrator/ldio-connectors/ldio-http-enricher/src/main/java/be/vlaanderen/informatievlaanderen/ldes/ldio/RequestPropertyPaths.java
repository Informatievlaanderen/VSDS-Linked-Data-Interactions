package be.vlaanderen.informatievlaanderen.ldes.ldio;

public record RequestPropertyPaths(String urlPropertyPath,
                                   String bodyPropertyPath,
                                   String headerPropertyPath,
                                   String payloadPropertyPath) {
}
