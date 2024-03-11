package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.Materialiser;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.ObserveConfiguration.ERROR_TEMPLATE;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

public class LdioRepositoryMaterialiser implements LdiOutput {
	private static final Logger log = LoggerFactory.getLogger(LdioRepositoryMaterialiser.class);
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
		if (!membersToCommit.isEmpty()) {
			materialiser.processAsync(List.copyOf(membersToCommit))
					.exceptionally(throwable -> {
						handleException(throwable);
						return null;
					});
			membersToCommit.clear();
		}
	}

	private void handleException(Throwable throwable) {
		log.atError().log(ERROR_TEMPLATE, "sendToMaterialiser", throwable.getMessage());
		final String fileName = "/tmp/materialisation/uncommitted-members-%s.ttl".formatted(LocalDateTime.now());
		final Model uncommitedMembersModel = ModelFactory.createDefaultModel();
		membersToCommit.forEach(uncommitedMembersModel::add);
		RDFWriter.source(uncommitedMembersModel)
				.lang(Lang.TURTLE)
				.output(fileName);
		log.atError().log("Uncommitted members can be found in file: {}", fileName);
	}

	private void resetExecutor() {
		scheduledExecutorService.shutdown();
		start();
	}
}
