package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.exceptions.HttpRequestException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.management.status.ClientStatusConsumer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioObserver;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.events.PipelineShutdownEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.status.PipelineStatusTrigger;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.status.StatusChangeSource;
import ldes.client.treenodesupplier.domain.valueobject.ClientStatus;
import ldes.client.treenodesupplier.domain.valueobject.EndOfLdesException;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

public class LdioLdesClient extends LdioInput {

	public static final String NAME = "Ldio:LdesClient";
	public static final String LDIO_SHUTDOWN_THREAD_NAME = "ldio-ldes-client-shutdown";

	private final Logger log = LoggerFactory.getLogger(LdioLdesClient.class);

	private final MemberSupplier memberSupplier;
	private boolean threadRunning = true;
	private final Supplier<Boolean> canGracefullyShutdownChecker;
	private boolean paused = false;
	private final boolean keepState;
	private final String pipelineName;
	private final ClientStatusConsumer clientStatusConsumer;

	public LdioLdesClient(ComponentExecutor componentExecutor,
	                      LdioObserver ldioObserver,
	                      MemberSupplier memberSupplier,
	                      ApplicationEventPublisher applicationEventPublisher,
	                      boolean keepState, ClientStatusConsumer clientStatusConsumer) {
		super(componentExecutor, null, ldioObserver, applicationEventPublisher);
		this.pipelineName = ldioObserver.getPipelineName();
		this.canGracefullyShutdownChecker = ldioObserver::hasProcessedAllData;
		this.memberSupplier = memberSupplier;
		this.keepState = keepState;
		this.clientStatusConsumer = clientStatusConsumer;
	}

	@Override
	public void start() {
		super.start();
		final ExecutorService executorService = newSingleThreadExecutor();
		executorService.submit(() -> {
			try {
				memberSupplier.init();
				this.run();
			} catch (RuntimeException e) {
				log.atWarn().log("HALTING pipeline because of an unhandled error");
				log.atError().log(e.getMessage());
				shutdownPipeline();
				throw e;
			}
		});
	}

	private synchronized void run() {
		try {
			while (threadRunning) {
				checkPause();
				processModel(memberSupplier.get().getModel());
			}
		} catch (EndOfLdesException e) {
			log.info("SHUTTING DOWN pipeline {} because end of LDES has been reached", pipelineName);
			shutdownPipeline();
		} catch (HttpRequestException e) {
			updateStatus(PipelineStatusTrigger.HALT, StatusChangeSource.AUTO);
			clientStatusConsumer.accept(ClientStatus.ERROR);
			log.error("LDES URL unavailable. Client paused: {}", e.getMessage());
			run();
		} catch (Exception e) {
			updateStatus(PipelineStatusTrigger.HALT, StatusChangeSource.AUTO);
			clientStatusConsumer.accept(ClientStatus.ERROR);
			log.error("LdesClientRunner FAILURE: {}", e.getMessage());
		}
	}

	private synchronized void checkPause() {
		while (paused) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				log.error("Thread interrupted: {}", e.getMessage());
				Thread.currentThread().interrupt();
			}
		}
	}

	@Override
	public void shutdown() {
		shutdownPipeline();
		if (!keepState) {
			memberSupplier.destroyState();
		}
	}

	@Override
	protected synchronized void resume() {
		this.paused = false;
		this.notifyAll();
	}

	@Override
	protected void pause() {
		this.paused = true;
	}

	private void shutdownPipeline() {
		try {
			Thread.ofVirtual().start(this::shutdownPipelineThread).join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private void shutdownPipelineThread() {
		threadRunning = false;
		do {
			try {
				Thread.sleep(Duration.ofSeconds(1));
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		} while (Boolean.FALSE.equals(canGracefullyShutdownChecker.get()));
		updateStatus(PipelineStatusTrigger.HALT);
		applicationEventPublisher.publishEvent(new PipelineShutdownEvent(pipelineName));
	}
}
