package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdiAzureBlobOut;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LdioAzureBlobOutAutoConfig {
	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldio.LdiAzureBlobOut")
	public LdioConfigurator ldioConfigurator() {
		return new LdioAzureBlobOutConfigurator();
	}

	public static class LdioAzureBlobOutConfigurator implements LdioConfigurator {
		public static final String PROPERTY_LANG = "lang";
		public static final String PROPERTY_STORAGE_ACCOUNT_NAME = "storage-account-name";
		public static final String PROPERTY_CONNECTION_STRING = "connection-string";
		public static final String PROPERTY_BLOB_CONTAINER = "blob-container";
		public static final String PROPERTY_JSON_CONTEXT_URI = "json-context-uri";
		private static final String DEFAULT_OUTPUT_LANG = "n-quads";
		public static final String DEFAULT_JSON_CONTEXT_URI = "";

		@Override
		public LdiComponent configure(ComponentProperties properties) {
			String outputLanguage = properties.getOptionalProperty(PROPERTY_LANG)
					.orElse(DEFAULT_OUTPUT_LANG);
			String storageAccountName = properties.getProperty(PROPERTY_STORAGE_ACCOUNT_NAME);
			String connectionString = properties.getProperty(PROPERTY_CONNECTION_STRING);
			String blobContainer = properties.getProperty(PROPERTY_BLOB_CONTAINER);
			String jsonContextURI = properties.getOptionalProperty(PROPERTY_JSON_CONTEXT_URI)
					.orElse(DEFAULT_JSON_CONTEXT_URI);
			return new LdiAzureBlobOut(outputLanguage, storageAccountName, connectionString, blobContainer,
					jsonContextURI);
		}
	}
}