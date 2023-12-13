package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.noauth;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutorSupplier;
import org.apache.http.Header;
import org.apache.http.impl.client.HttpClientBuilder;

import java.util.ArrayList;
import java.util.Collection;

public class DefaultConfig implements RequestExecutorSupplier {

    public RequestExecutor createRequestExecutor(Collection<Header> customHeaders) {
        return createRequestExecutor(new ArrayList<>(), customHeaders);
    }

    public RequestExecutor createRequestExecutor(Collection<Header> headers, Collection<Header> customHeaders) {
        return new DefaultRequestExecutor(
                HttpClientBuilder.create().setDefaultHeaders(headers).disableRedirectHandling().build(), customHeaders);
    }

}
