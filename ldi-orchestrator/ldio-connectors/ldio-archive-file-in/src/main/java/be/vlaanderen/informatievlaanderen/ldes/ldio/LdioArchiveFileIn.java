package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdioArchiveFileIn extends LdiInput {

    private final Logger log = LoggerFactory.getLogger(LdioArchiveFileIn.class);

    public LdioArchiveFileIn(ComponentExecutor executor, LdioArchiveFileInRunner archiveFileInRunner) {
        super(executor, null);
        log.info("Starting with crawling the archive.");
        archiveFileInRunner.run();
        log.info("Finished crawling the archive.");
    }

}
