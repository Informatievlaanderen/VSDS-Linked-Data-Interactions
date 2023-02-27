package be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.endpoint;

public record ApiKey(String header, String key) {

    public static ApiKey empty() {
        return new ApiKey("", "");
    }

}
