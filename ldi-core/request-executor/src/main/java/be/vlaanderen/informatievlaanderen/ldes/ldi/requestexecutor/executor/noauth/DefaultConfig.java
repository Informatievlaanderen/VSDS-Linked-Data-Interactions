package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.noauth;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutorSupplier;
import org.apache.http.Header;
import org.apache.http.impl.client.HttpClientBuilder;

import java.util.Collection;

public class DefaultConfig implements RequestExecutorSupplier {

	private final Collection<Header> headers;

	public DefaultConfig(Collection<Header> headers) {
		this.headers = headers;
	}

	public RequestExecutor createRequestExecutor() {
		return new DefaultRequestExecutor(
				HttpClientBuilder.create().setDefaultHeaders(headers).disableRedirectHandling().build());
	}

}
