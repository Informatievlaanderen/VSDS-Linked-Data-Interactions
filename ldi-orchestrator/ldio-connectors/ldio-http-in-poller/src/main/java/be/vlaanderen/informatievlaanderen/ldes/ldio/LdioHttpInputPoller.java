package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Request;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioHttpInputPollerProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exceptions.MissingHeaderException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exceptions.UnsuccesfulPollingException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.PollingInterval.TYPE.CRON;

public class LdioHttpInputPoller extends LdioInput implements Runnable {
	public static final String NAME = "Ldio:HttpInPoller";
	private static final String CONTENT_TYPE = "Content-Type";
	private static final Logger log = LoggerFactory.getLogger(LdioHttpInputPoller.class);
	private final ThreadPoolTaskScheduler scheduler;
	private final RequestExecutor requestExecutor;
	private final LdioHttpInputPollerProperties properties;

	private ScheduledFuture<?> scheduledPoll;

	public LdioHttpInputPoller(ComponentExecutor executor, LdiAdapter adapter, LdioObserver ldioObserver,
							   RequestExecutor requestExecutor, LdioHttpInputPollerProperties properties) {
		super(executor, adapter, ldioObserver);
		this.requestExecutor = requestExecutor;
		this.properties = properties;
		this.scheduler = new ThreadPoolTaskScheduler();
		this.scheduler.setWaitForTasksToCompleteOnShutdown(false);
		this.scheduler.setErrorHandler(t -> log.error(t.getMessage()));
	}

	@Override
	public void start() {
		startScheduler();
	}

	private void startScheduler() {
		scheduler.initialize();
		final var pollingInterval = properties.getPollingInterval();
		if (pollingInterval.getType() == CRON) {
			scheduledPoll = scheduler.schedule(this, pollingInterval.getCronTrigger());
		} else {
			scheduledPoll = scheduler.scheduleAtFixedRate(this, Instant.now(), pollingInterval.getDuration());
		}
	}

	@Override
	public void run() {
		properties.getRequests().forEach(request -> {
			try {
				executeRequest(request);
			} catch (Exception e) {
				if (!properties.isContinueOnFailEnabled()) {
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
			log.error("Failed to execute request {} {} {}", request.getMethod(), request.getUrl(), response.getHttpStatus());
			response.getBodyAsString().ifPresent(log::error);
			throw new UnsuccesfulPollingException(response.getHttpStatus(), request.getUrl());
		}
	}

	@Override
	public synchronized void resume() {
		if (properties.getPollingInterval() != null) {
			startScheduler();
		}
	}

	@Override
	public synchronized void pause() {
		scheduledPoll.cancel(false);
    }
}
