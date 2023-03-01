package be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.endpoint;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public record ApiKey(String header, String key) {

    public static ApiKey empty() {
        return new ApiKey("", "");
    }

    public boolean isNotEmpty() {
        return isNotBlank(header) && isNotBlank(key);
    }

}
