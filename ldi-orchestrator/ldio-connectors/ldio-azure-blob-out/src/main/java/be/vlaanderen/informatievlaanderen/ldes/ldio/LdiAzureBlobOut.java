package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.conversionstrategy.ConversionStrategy;
import be.vlaanderen.informatievlaanderen.ldes.ldio.conversionstrategy.ConversionStrategyFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldio.util.MemberIdExtractor;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.specialized.BlockBlobClient;
import org.apache.jena.rdf.model.Model;

import java.io.ByteArrayInputStream;

@SuppressWarnings("java:S2629")
public class LdiAzureBlobOut implements LdiOutput {
	private final String outputLanguage;
	private final String storageAccountName;
	private final String connectionString;
	private final String blobContainer;
	private final String jsonContextURI;
	private final MemberIdExtractor memberIdExtractor = new MemberIdExtractor();
	private final ConversionStrategyFactory conversionStrategyFactory = new ConversionStrategyFactory();

	public LdiAzureBlobOut(String outputLanguage, String storageAccountName, String connectionString,
			String blobContainer, String jsonContextURI) {
		this.outputLanguage = outputLanguage;
		this.storageAccountName = storageAccountName;
		this.connectionString = connectionString;
		this.blobContainer = blobContainer;
		this.jsonContextURI = jsonContextURI;
	}

	@Override
	public void accept(Model model) {
		ConversionStrategy conversionStrategy = conversionStrategyFactory.getConversionStrategy(outputLanguage,
				jsonContextURI);
		BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
				.endpoint("https://" + storageAccountName + ".blob.core.windows.net/")
				.connectionString(connectionString)
				.buildClient();

		BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient(blobContainer);
		BlockBlobClient blockBlobClient = blobContainerClient
				.getBlobClient(memberIdExtractor.extractMemberId(model) + "." + conversionStrategy.getFileExtension())
				.getBlockBlobClient();

		String content = conversionStrategy.getContent(model);
		ByteArrayInputStream dataStream = new ByteArrayInputStream(content.getBytes());
		blockBlobClient.upload(dataStream, content.length());
	}
}
