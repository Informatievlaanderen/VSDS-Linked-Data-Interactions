package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.Materialiser;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.ZonedDateTime;
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
			final List<Model> members = List.copyOf(membersToCommit);
			materialiser.processAsync(members)
					.exceptionally(throwable -> {
						handleException(throwable, members);
						return null;
					});
			membersToCommit.clear();
		}
	}

	private void handleException(Throwable throwable, List<Model> failedMembers) {
		log.atError().log(ERROR_TEMPLATE, "sendToMaterialiser", throwable.getMessage());
		final Model uncommitedMembersModel = ModelFactory.createDefaultModel();
		failedMembers.forEach(uncommitedMembersModel::add);
		final File materialisationFolder = new File("materialisation");
		if (materialisationFolder.exists() || materialisationFolder.mkdir()) {
			final String fileName = "uncommitted-members-%s.ttl".formatted(ZonedDateTime.now().toEpochSecond());
			final File uncommittedMembersFile = new File(materialisationFolder, fileName);
			RDFWriter.source(uncommitedMembersModel).lang(Lang.TURTLE).output(uncommittedMembersFile.getPath());
			log.atError().log("Uncommitted members can be found in file: {}", uncommittedMembersFile.getAbsolutePath());
		} else {
			final String uncommittedMembers = RDFWriter.source(uncommitedMembersModel).lang(Lang.TURTLE).asString();
			log.atError().log("Unable to commit the following members: \n {}", uncommittedMembers);
		}
	}

	private void resetExecutor() {
		scheduledExecutorService.shutdown();
		start();
	}
}
