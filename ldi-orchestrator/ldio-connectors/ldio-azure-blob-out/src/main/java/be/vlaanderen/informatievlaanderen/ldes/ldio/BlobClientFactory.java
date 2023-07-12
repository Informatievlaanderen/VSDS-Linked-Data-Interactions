package be.vlaanderen.informatievlaanderen.ldes.ldio;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

public class BlobClientFactory {

	private final BlobContainerClient blobContainerClient;
	private static BlobClientFactory instance = null;

	private BlobClientFactory(BlobContainerClient blobContainerClient) {
		this.blobContainerClient = blobContainerClient;
	}

	public static synchronized BlobClientFactory getInstance(String storageAccountName, String connectionString,
			String blobContainer) {
		if (instance == null) {
			BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
					.endpoint("https://" + storageAccountName + ".blob.core.windows.net/")
					.connectionString(connectionString)
					.buildClient();

			instance = new BlobClientFactory(blobServiceClient.getBlobContainerClient(blobContainer));
		}

		return instance;
	}

	public BlobContainerClient getBlobContainerClient() {
		return blobContainerClient;
	}
}
