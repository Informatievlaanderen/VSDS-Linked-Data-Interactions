package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Request;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeaders;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exceptions.MissingHeaderException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exceptions.UnsuccesfulPollingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class HttpInputPoller extends LdiInput {
	private final ScheduledExecutorService scheduler;
	private final RequestExecutor requestExecutor;
	private final List<Request> requests;
	private final boolean continueOnFail;
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpInputPoller.class);
	private static final String CONTENT_TYPE = "Content-Type";

	public HttpInputPoller(ComponentExecutor executor, LdiAdapter adapter, List<String> endpoints,
			boolean continueOnFail) {
		super(executor, adapter);
		RequestExecutorFactory requestExecutorFactory = new RequestExecutorFactory();
		this.requestExecutor = requestExecutorFactory.createNoAuthExecutor();
		this.requests = endpoints.stream().map(endpoint -> new Request(endpoint, RequestHeaders.empty())).toList();
		this.continueOnFail = continueOnFail;
		this.scheduler = Executors.newSingleThreadScheduledExecutor();
	}

	public void schedulePoller(long interval) {
		scheduler.scheduleAtFixedRate(this::poll, 0, interval, TimeUnit.SECONDS);
	}

	public void poll() {
		try {
			requests.forEach(this::executeRequest);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			if (!continueOnFail) {
				throw e;
			}
		}
	}

	private void executeRequest(Request request) {
		System.out.println(request.getUrl());
		Response response = requestExecutor.execute(request);
		if (HttpStatusCode.valueOf(response.getHttpStatus()).is2xxSuccessful()) {
			String contentType = response.getFirstHeaderValue(CONTENT_TYPE)
					.orElseThrow(() -> new MissingHeaderException(response.getHttpStatus(), request.getUrl()));
			String content = response.getBody().orElseThrow();
			getAdapter().apply(LdiAdapter.Content.of(content, contentType))
					.forEach(getExecutor()::transformLinkedData);
		} else {
			throw new UnsuccesfulPollingException(response.getHttpStatus(), request.getUrl());
		}
	}
}
