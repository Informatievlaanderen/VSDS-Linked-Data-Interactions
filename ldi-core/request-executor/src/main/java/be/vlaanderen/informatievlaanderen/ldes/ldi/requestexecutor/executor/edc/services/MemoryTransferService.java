package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.PostRequest;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeader;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeaders;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;

import java.util.List;

public class MemoryTransferService implements TransferService {

	private final RequestExecutor requestExecutor;
	private final String consumerConnectorUrl;
	private String transfer;

	public MemoryTransferService(RequestExecutor requestExecutor, String consumerConnectorUrl) {
		this.requestExecutor = requestExecutor;
		this.consumerConnectorUrl = consumerConnectorUrl;
	}

	@Override
	public void startTransfer(String transfer) {
		this.transfer = transfer;
		sendTransferRequest();
	}

	@Override
	public void refreshTransfer() {
		if (transfer == null) {
			throw new IllegalStateException("A transfer needs to be started before it can be refreshed!");
		}

		sendTransferRequest();
	}

	private void sendTransferRequest() {
		var contentType = new RequestHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
		var requestHeaders = new RequestHeaders(List.of(contentType));
		var request = new PostRequest(consumerConnectorUrl, requestHeaders, transfer);
		this.requestExecutor.execute(request);
	}

}
