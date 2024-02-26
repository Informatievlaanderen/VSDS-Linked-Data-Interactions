package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.GetRequest;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Request;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeaders;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PollingInterval;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exceptions.MissingHeaderException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exceptions.UnsuccesfulPollingException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.PollingInterval.TYPE.CRON;

public class LdioHttpInputPoller extends LdioInput implements Runnable {
	public static final String NAME = "Ldio:HttpInPoller";
	private final ThreadPoolTaskScheduler scheduler;
	private final RequestExecutor requestExecutor;
	private final List<? extends Request> requests;
	private final boolean continueOnFail;
	private static final Logger log = LoggerFactory.getLogger(LdioHttpInputPoller.class);
	private static final String CONTENT_TYPE = "Content-Type";
	private PollingInterval pollingInterval;
	private ScheduledFuture scheduledPoll;

	public LdioHttpInputPoller(String pipelineName, ComponentExecutor executor, LdiAdapter adapter, ObservationRegistry observationRegistry, List<String> endpoints,
							   boolean continueOnFail, RequestExecutor requestExecutor, ApplicationEventPublisher applicationEventPublisher) {
		super(NAME, pipelineName, executor, adapter, observationRegistry, applicationEventPublisher);
		this.requestExecutor = requestExecutor;
		this.requests = endpoints.stream().map(endpoint -> new GetRequest(endpoint, RequestHeaders.empty())).toList();
		this.continueOnFail = continueOnFail;
		this.scheduler = new ThreadPoolTaskScheduler();
		this.scheduler.setWaitForTasksToCompleteOnShutdown(false);
		this.scheduler.setErrorHandler(t -> log.error(t.getMessage()));
	}

	public void schedulePoller(PollingInterval pollingInterval) {
		this.pollingInterval = pollingInterval;
		scheduler.initialize();
		schedule(pollingInterval);
	}

	private void schedule(PollingInterval pollingInterval) {
		if (pollingInterval.getType() == CRON) {
			scheduledPoll = scheduler.schedule(this, pollingInterval.getCronTrigger());
		} else {
			scheduledPoll = scheduler.scheduleAtFixedRate(this, Instant.now(), pollingInterval.getDuration());
		}
	}

	@Override
	public void run() {
		requests.forEach(request -> {
			try {
				executeRequest(request);
			} catch (Exception e) {
				if (!continueOnFail) {
					throw e;
				}
			}
		});
	}

	public void shutdown() {
		this.scheduler.destroy();
	}

	private void executeRequest(Request request) {
		log.atDebug().log("Polling next url: {}", request.getUrl());

		Response response = requestExecutor.execute(request);

		log.debug("{} {} {}", request.getMethod(), request.getUrl(), response.getHttpStatus());

		if (HttpStatusCode.valueOf(response.getHttpStatus()).is2xxSuccessful()) {
			String contentType = response.getFirstHeaderValue(CONTENT_TYPE)
					.orElseThrow(() -> new MissingHeaderException(response.getHttpStatus(), request.getUrl()));
			String content = response.getBodyAsString().orElseThrow();
			processInput(content, contentType);
		} else {
			throw new UnsuccesfulPollingException(response.getHttpStatus(), request.getUrl());
		}
	}

	@Override
	protected synchronized void resume() {
		if (pollingInterval != null) {
			schedule(pollingInterval);
		}
	}

	@Override
	protected synchronized void pause() {
		scheduledPoll.cancel(false);
    }
}
