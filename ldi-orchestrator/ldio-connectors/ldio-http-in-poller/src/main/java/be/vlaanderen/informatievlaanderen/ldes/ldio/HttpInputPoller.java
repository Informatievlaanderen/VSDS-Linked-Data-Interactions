package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.GetRequest;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Request;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeaders;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exceptions.MissingHeaderException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exceptions.UnsuccesfulPollingException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HttpInputPoller extends LdioInput {
	public static final String NAME = "be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpInPoller";
	private final ScheduledExecutorService scheduler;
	private final RequestExecutor requestExecutor;
	private final List<? extends Request> requests;
	private final boolean continueOnFail;
	private static final Logger log = LoggerFactory.getLogger(HttpInputPoller.class);
	private static final String CONTENT_TYPE = "Content-Type";

	public HttpInputPoller(String pipelineName, ComponentExecutor executor, LdiAdapter adapter, List<String> endpoints,
	                       boolean continueOnFail, RequestExecutor requestExecutor) {
		super(NAME, pipelineName, executor, adapter);
		this.requestExecutor = requestExecutor;
		this.requests = endpoints.stream().map(endpoint -> new GetRequest(endpoint, RequestHeaders.empty())).toList();
		this.continueOnFail = continueOnFail;
		this.scheduler = Executors.newSingleThreadScheduledExecutor();
	}

	public void schedulePoller(long interval) {
		scheduler.scheduleAtFixedRate(this::poll, 0, interval, TimeUnit.SECONDS);
	}

	public void poll() {
		requests.forEach(request -> {
			try {
				executeRequest(request);
			} catch (Exception e) {
				log.error(e.getMessage());
				if (!continueOnFail) {
					throw e;
				}
			}
		});
	}

	private void executeRequest(Request request) {
		log.atDebug().log("Polling next url: {}", request.getUrl());

		Response response = requestExecutor.execute(request);

		log.debug(request.getMethod() + " " + request.getUrl() + " " + response.getHttpStatus());

		if (HttpStatusCode.valueOf(response.getHttpStatus()).is2xxSuccessful()) {
			String contentType = response.getFirstHeaderValue(CONTENT_TYPE)
					.orElseThrow(() -> new MissingHeaderException(response.getHttpStatus(), request.getUrl()));
			String content = response.getBody().orElseThrow();
			processInput(content, contentType);
		} else {
			throw new UnsuccesfulPollingException(response.getHttpStatus(), request.getUrl());
		}
	}
}
