package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.configurator.ComponentProperties;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.AmqpConfig.*;

public class RemoteUrlExtractor {

    private final ComponentProperties config;

    public RemoteUrlExtractor(ComponentProperties config) {
        this.config = config;
    }

    String getRemoteUrl() {
        String remoteUrl = config.getProperty(REMOTE_URL);

        Pattern pattern = Pattern.compile(REMOTE_URL_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(remoteUrl);
        boolean matchFound = matcher.find();
        if (matchFound) {
            return remoteUrl;
        } else {
            throw new IllegalArgumentException(REMOTE_URL_REGEX_ERROR);
        }
    }

}
