package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdiAzureBlobOut;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdiAzureBlobOut.NAME;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LdioAzureBlobOutAutoConfigTest {
	public static final String PROPERTY_LANG = "lang";
	public static final String PROPERTY_STORAGE_ACCOUNT_NAME = "storage-account-name";
	public static final String PROPERTY_CONNECTION_STRING = "connection-string";
	public static final String PROPERTY_BLOB_CONTAINER = "blob-container";
	public static final String PROPERTY_JSON_CONTEXT_URI = "json-context-uri";

	@Test
	void when_PropertiesAreSet_then_LdiAzureBlobOutIsCreated() {
		Map<String, String> properties = Map.of(
				PROPERTY_LANG, "lang",
				PROPERTY_STORAGE_ACCOUNT_NAME, "storageAccountName",
				PROPERTY_CONNECTION_STRING, "connectionString",
				PROPERTY_BLOB_CONTAINER, "blobContainer",
				PROPERTY_JSON_CONTEXT_URI, "jsonContextUri");
		ComponentProperties componentProperties = new ComponentProperties("pipeline", NAME, properties);
		LdiComponent ldiComponent = new LdioAzureBlobOutAutoConfig.LdioAzureBlobOutConfigurator()
				.configure(componentProperties);

		assertTrue(ldiComponent instanceof LdiAzureBlobOut);
	}

}