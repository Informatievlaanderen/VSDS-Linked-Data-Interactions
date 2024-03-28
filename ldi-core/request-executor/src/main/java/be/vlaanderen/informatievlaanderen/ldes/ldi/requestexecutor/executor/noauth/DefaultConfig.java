package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.noauth;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutorSupplier;
import org.apache.http.Header;
import org.apache.http.impl.client.HttpClientBuilder;

import java.util.Collection;

public class DefaultConfig implements RequestExecutorSupplier {

	private final Collection<Header> headers;
	private final boolean enableRedirectHandling;

	public DefaultConfig(Collection<Header> headers, boolean enableRedirectHandling) {
		this.headers = headers;
        this.enableRedirectHandling = enableRedirectHandling;
    }

	public RequestExecutor createRequestExecutor() {
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		if (!enableRedirectHandling) {
			httpClientBuilder.disableRedirectHandling();
		}

		return new DefaultRequestExecutor(httpClientBuilder.setDefaultHeaders(headers).build());
	}

}
