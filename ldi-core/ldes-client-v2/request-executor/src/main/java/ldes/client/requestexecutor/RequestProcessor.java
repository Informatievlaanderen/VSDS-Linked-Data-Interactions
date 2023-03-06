package ldes.client.requestexecutor;

import ldes.client.requestexecutor.config.ClientCredentialsConfig;
import ldes.client.requestexecutor.domain.valueobjects.Request;
import ldes.client.requestexecutor.domain.valueobjects.Response;
import ldes.client.requestexecutor.executor.RequestExecutorFactory;
import ldes.client.requestexecutor.executor.clientcredentials.ClientCredentialsRequestExecutor;
import ldes.client.requestexecutor.executor.noauth.DefaultRequestExecutor;

public class RequestProcessor {

	// TODO: 6/03/2023 remove this class
	public Response processRequest(final Request request) {
		// Insert Retry Logic
		RequestExecutorFactory factory = new RequestExecutorFactory();

		ClientCredentialsRequestExecutor noAuthRequestExecutor =
				factory.createClientCredentialsRequestExecutor(
						new ClientCredentialsConfig("id", "secret",
								"http://localhost:8000/token", "scope"));

//		DefaultRequestExecutor noAuthRequestExecutor = factory.createNoAuthRequestExecutor();
		return noAuthRequestExecutor.apply(request);
	}
}
