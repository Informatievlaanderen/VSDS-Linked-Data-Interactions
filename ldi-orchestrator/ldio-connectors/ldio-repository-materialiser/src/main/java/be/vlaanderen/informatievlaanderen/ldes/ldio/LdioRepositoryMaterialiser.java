package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.Materialiser;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import org.apache.jena.rdf.model.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

public class LdioRepositoryMaterialiser implements LdiOutput {
	public static final String NAME = "Ldio:RepositoryMaterialiser";
	private ScheduledExecutorService scheduledExecutorService;
	private final int batchSize;
	private final int batchTimeout;
	private final Materialiser materialiser;
	private final List<Model> membersToCommit;

	public LdioRepositoryMaterialiser(Materialiser materialiser, int batchSize, int batchTimeout) {
		this.materialiser = materialiser;
		this.batchSize = batchSize;
		this.batchTimeout = batchTimeout;
		this.membersToCommit = new ArrayList<>();
	}

	@Override
	public void accept(Model model) {
		membersToCommit.add(model);
		if (membersToCommit.size() >= batchSize) {
			sendToMaterialiser();
			resetExecutor();
		}
	}

	public void start() {
		scheduledExecutorService = newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(this::sendToMaterialiser, batchTimeout, batchTimeout, TimeUnit.MILLISECONDS);
	}

	public void shutdown() {
		materialiser.shutdown();
		scheduledExecutorService.shutdown();
	}

	public synchronized void sendToMaterialiser() {
		if(!membersToCommit.isEmpty()) {
			materialiser.process(membersToCommit);
			membersToCommit.clear();
		}
	}

	private void resetExecutor() {
		scheduledExecutorService.shutdownNow();
		start();
	}
}
