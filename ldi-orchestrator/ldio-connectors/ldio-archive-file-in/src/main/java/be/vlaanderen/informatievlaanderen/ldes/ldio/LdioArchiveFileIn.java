package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.components.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.services.LdioObserver;
import io.micrometer.observation.ObservationRegistry;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

public class LdioArchiveFileIn extends LdioInput {
	public static final String NAME = "Ldio:ArchiveFileIn";
	private final Logger log = LoggerFactory.getLogger(LdioArchiveFileIn.class);
	private final ArchiveFileCrawler archiveFileCrawler;
	private final Lang sourceFormat;
	private boolean paused = false;

	public LdioArchiveFileIn(String pipelineName, ComponentExecutor executor, ObservationRegistry observationRegistry, ApplicationEventPublisher applicationEventPublisher, ArchiveFileCrawler crawler, Lang source) {
		super(executor, null, LdioObserver.register(NAME, pipelineName, observationRegistry), applicationEventPublisher);
		this.archiveFileCrawler = crawler;
		this.sourceFormat = source;
		start();
		log.info("Starting with crawling the archive.");
		crawlArchive();
		log.info("Finished crawling the archive.");
	}


	@SuppressWarnings("java:S2273")
	public synchronized void crawlArchive() {
		archiveFileCrawler.streamArchiveFilePaths().forEach(file -> {
			while (paused) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
					log.error("Thread interrupted: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
			Model model = RDFParser.source(file).lang(sourceFormat).toModel();
			processModel(model);
		});
	}

	@Override
	public void shutdown() {
		this.paused = true;
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
