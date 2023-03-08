package ldes.client.requestexecutor.domain.valueobjects;

import ldes.client.requestexecutor.executor.RequestExecutor;
import ldes.client.requestexecutor.executor.noauth.DefaultRequestExecutor;
import org.apache.http.Header;
import org.apache.http.impl.client.HttpClientBuilder;

import java.util.ArrayList;
import java.util.Collection;

public class DefaultConfig implements RequestExecutorSupplier {

	public RequestExecutor createRequestExecutor() {
		return createRequestExecutor(new ArrayList<>());
	}

	public RequestExecutor createRequestExecutor(Collection<Header> headers) {
		return new DefaultRequestExecutor(
				HttpClientBuilder.create().setDefaultHeaders(headers).disableRedirectHandling().build());
	}

}
