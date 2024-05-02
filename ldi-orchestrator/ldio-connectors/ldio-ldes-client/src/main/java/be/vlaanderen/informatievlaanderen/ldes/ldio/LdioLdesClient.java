package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineStatusEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.HaltedPipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.PipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioObserver;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.StatusChangeSource;
import io.micrometer.observation.ObservationRegistry;
import ldes.client.treenodesupplier.domain.valueobject.EndOfLdesException;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

public class LdioLdesClient extends LdioInput {

	public static final String NAME = "Ldio:LdesClient";

	private final Logger log = LoggerFactory.getLogger(LdioLdesClient.class);

	private final MemberSupplier memberSupplier;
	private boolean threadRunning = true;
	private boolean paused = false;
	private final String pipelineName;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final ThreadPoolTaskScheduler scheduler;

	public LdioLdesClient(ComponentExecutor componentExecutor,
						  String pipelineName,
						  ObservationRegistry observationRegistry,
						  MemberSupplier memberSupplier,
						  ApplicationEventPublisher applicationEventPublisher) {
		super(componentExecutor, null, LdioObserver.register(NAME, pipelineName, observationRegistry));
		this.pipelineName = pipelineName;
		this.memberSupplier = memberSupplier;
		this.applicationEventPublisher = applicationEventPublisher;
		this.scheduler = new ThreadPoolTaskScheduler();
		this.scheduler.setWaitForTasksToCompleteOnShutdown(false);
		this.scheduler.setErrorHandler(this::handleError);
	}

	@Override
	public void start() {
		scheduler.initialize();
		scheduler.submit(() -> {
			memberSupplier.init();
			this.run();
		});
	}

	private synchronized void run() {
		try {
			while (threadRunning) {
				checkPause();
				processModel(memberSupplier.get().getModel());
			}
		} catch (EndOfLdesException e) {
			log.warn(e.getMessage());
		} catch (Exception e) {
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

	protected void updateStatus(PipelineStatus status) {
		applicationEventPublisher.publishEvent(new PipelineStatusEvent(pipelineName, status, StatusChangeSource.AUTO));
	}

	@Override
	public void shutdown() {
		threadRunning = false;
		scheduler.destroy();
		memberSupplier.destroyState();
	}

	@Override
	public synchronized void resume() {
		this.paused = false;
		this.notifyAll();
	}

	@Override
	public void pause() {
		this.paused = true;
	}

	private void handleError(Throwable e) {
		log.atWarn().log("HALTING pipeline because of an unhandled error");
		log.atError().log(e.getMessage());
		updateStatus(new HaltedPipelineStatus());
	}
}
