package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdioArchiveFileIn extends LdioInput {

	private final Logger log = LoggerFactory.getLogger(LdioArchiveFileIn.class);

	public LdioArchiveFileIn(String name, String pipelineName, ComponentExecutor executor, LdioArchiveFileInRunner archiveFileInRunner) {
		super(name, pipelineName, executor, null);
		log.info("Starting with crawling the archive.");
		archiveFileInRunner.run();
		log.info("Finished crawling the archive.");
	}

}
