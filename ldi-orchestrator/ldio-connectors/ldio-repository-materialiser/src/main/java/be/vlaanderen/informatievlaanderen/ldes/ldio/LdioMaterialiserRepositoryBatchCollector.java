package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.Materialiser;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import org.apache.jena.rdf.model.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

public class LdioMaterialiserRepositoryBatchCollector implements LdiComponent {
	private ScheduledExecutorService scheduledExecutorService;
	private final int batchSize;
	private final int batchTimeout;
	private final Materialiser materialiser;
	private final List<Model> membersToCommit;

	public LdioMaterialiserRepositoryBatchCollector(int batchSize, int batchTimeout, Materialiser materialiser) {
		this.batchSize = batchSize;
		this.batchTimeout = batchTimeout;
		this.materialiser = materialiser;
		this.membersToCommit = new ArrayList<>();
		initExecutor();
	}

	public void addMemberToCommit(Model model) {
		synchronized (membersToCommit) {
			membersToCommit.add(model);
			if (membersToCommit.size() >= batchSize) {
				sendToMaterialiser();
				resetExecutor();
			}
		}
	}

	public void shutdown() {
		materialiser.shutdown();
		scheduledExecutorService.shutdown();
	}

	public synchronized void sendToMaterialiser() {
		materialiser.process(membersToCommit);
		membersToCommit.clear();
	}

	private void initExecutor() {
		scheduledExecutorService = newSingleThreadScheduledExecutor();
		scheduledExecutorService.schedule(this::sendToMaterialiser, batchTimeout, TimeUnit.MILLISECONDS);
	}

	private void resetExecutor() {
		scheduledExecutorService.shutdownNow();
		initExecutor();
	}
}
