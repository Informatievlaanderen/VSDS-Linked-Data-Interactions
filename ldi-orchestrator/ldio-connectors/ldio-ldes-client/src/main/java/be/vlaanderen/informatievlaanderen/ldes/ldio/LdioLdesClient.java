package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import io.micrometer.observation.ObservationRegistry;
import ldes.client.treenodesupplier.MemberSupplier;
import ldes.client.treenodesupplier.domain.valueobject.EndOfLdesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

public class LdioLdesClient extends LdioInput {

	public static final String NAME = "Ldio:LdesClient";

	private final Logger log = LoggerFactory.getLogger(LdioLdesClient.class);

	private final MemberSupplier memberSupplier;
	private boolean threadRunning = true;

	public LdioLdesClient(String pipelineName,
						  ComponentExecutor componentExecutor,
						  ObservationRegistry observationRegistry,
						  MemberSupplier memberSupplier,
						  ApplicationEventPublisher applicationEventPublisher) {
		super(NAME, pipelineName, componentExecutor, null, observationRegistry, applicationEventPublisher);
		this.memberSupplier = memberSupplier;
	}

	@SuppressWarnings("java:S2095")
	public void start() {
		final ExecutorService executorService = newSingleThreadExecutor();
		executorService.submit(this::run);
	}

	private synchronized void run() {
		try {
			while (threadRunning) {
				while (this.isHalted()) {
					try {
						this.wait();
					}  catch (InterruptedException e) {
						log.error("Thread interrupted: {}", e.getMessage());
						Thread.currentThread().interrupt();
					}
				}
				processModel(memberSupplier.get().getModel());
			}
		} catch (EndOfLdesException e) {
			log.warn(e.getMessage());
		} catch (Exception e) {
			log.error("LdesClientRunner FAILURE", e);
		}
	}

	@Override
	public void shutdown() {
		threadRunning = false;
	}

	@Override
	protected synchronized void resume() {
		this.notifyAll();
	}

	@Override
	protected void pause() {
		//Handled by status check
	}
}
