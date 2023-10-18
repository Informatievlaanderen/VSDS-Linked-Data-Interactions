package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.EmptyPropertyExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.PropertyExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.PropertyPathExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.RequestPropertyPathExtractors;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioHttpEnricherProperties.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioHttpEnricherProperties.HTTP_METHOD_PROPERTY_PATH;

class PropertyPathExtractorConverter {

    private final ComponentProperties config;

    PropertyPathExtractorConverter(ComponentProperties config) {
        this.config = config;
    }

    RequestPropertyPathExtractors mapToPropertyPathExtractors() {
        final var urlPropertyPathExtractor = PropertyPathExtractor.from(config.getProperty(URL_PROPERTY_PATH));
        final var bodyPropertyPathExtractor = createPropertyPathExtractor(BODY_PROPERTY_PATH);
        final var headerPropertyPathExtractor = createPropertyPathExtractor(HEADER_PROPERTY_PATH);
        final var httpMethodPropertyPathExtractor = createPropertyPathExtractor(HTTP_METHOD_PROPERTY_PATH);
        return new RequestPropertyPathExtractors(
                urlPropertyPathExtractor,
                bodyPropertyPathExtractor,
                headerPropertyPathExtractor,
                httpMethodPropertyPathExtractor);
    }

    private PropertyExtractor createPropertyPathExtractor(String property) {
        return config
                .getOptionalProperty(property)
                .map(PropertyPathExtractor::from)
                .map(PropertyExtractor.class::cast)
                .orElse(new EmptyPropertyExtractor());
    }

}
