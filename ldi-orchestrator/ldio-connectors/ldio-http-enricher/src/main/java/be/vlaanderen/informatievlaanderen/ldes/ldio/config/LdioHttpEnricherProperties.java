package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

public class LdioHttpEnricherProperties {

    private LdioHttpEnricherProperties() {
    }

    public static final String HTTP_REQUESTER_ADAPTER = "http-requester-adapter";
    public static final String URL_PROPERTY_PATH = "url-property-path";
    public static final String HEADER_PROPERTY_PATH = "header-property-path";
    public static final String PAYLOAD_PROPERTY_PATH = "payload-property-path";
    public static final String BODY_PROPERTY_PATH = "body-property-path"; // TODO TVB: 16/10/23 dit en content type nog implementeren

}
