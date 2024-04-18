package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioObserver;
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
	private boolean paused = false;
	private final boolean keepState;

	public LdioLdesClient(ComponentExecutor componentExecutor,
                          LdioObserver ldioObserver,
                          MemberSupplier memberSupplier,
                          ApplicationEventPublisher applicationEventPublisher,
						  boolean keepState) {
		super(componentExecutor, null, ldioObserver, applicationEventPublisher);
		this.memberSupplier = memberSupplier;
        this.keepState = keepState;
    }

	@Override
	public void start() {
		super.start();
		final ExecutorService executorService = newSingleThreadExecutor();
		executorService.submit(() -> {
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
			log.error("LdesClientRunner FAILURE", e);
		}
	}

	private synchronized void checkPause() {
		while (paused) {
			try {
				this.wait();
			}  catch (InterruptedException e) {
				log.error("Thread interrupted: {}", e.getMessage());
				Thread.currentThread().interrupt();
			}
		}
	}

	@Override
	public void shutdown() {
		threadRunning = false;
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
}
