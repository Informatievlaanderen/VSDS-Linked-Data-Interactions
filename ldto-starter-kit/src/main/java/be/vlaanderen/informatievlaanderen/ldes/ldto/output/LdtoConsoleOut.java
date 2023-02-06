package be.vlaanderen.informatievlaanderen.ldes.ldto.output;

import be.vlaanderen.informatievlaanderen.ldes.ldto.config.OrchestratorConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldto.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.ldto.types.LdtoOutput;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import java.util.Objects;

import static be.vlaanderen.informatievlaanderen.ldes.ldto.LdtoConstants.CONTENT_TYPE;
import static be.vlaanderen.informatievlaanderen.ldes.ldto.LdtoConstants.DEFAULT_OUTPUT_LANG;
import static be.vlaanderen.informatievlaanderen.ldes.ldto.converter.RdfModelConverter.getLang;

public class LdtoConsoleOut implements LdtoOutput {
	private final Logger LOGGER = LoggerFactory.getLogger(LdtoConsoleOut.class);

	private Lang outputLanguage = DEFAULT_OUTPUT_LANG;

	public LdtoConsoleOut(OrchestratorConfig config) {
		if (config.getOutput().getConfig().containsKey(CONTENT_TYPE)) {
			outputLanguage = getLang(
					Objects.requireNonNull(MediaType.valueOf(config.getOutput().getConfig().get(CONTENT_TYPE))));
		}
	}

	@Override
	public void sendLinkedData(Model linkedDataModel) {
		LOGGER.info(RdfModelConverter.toString(linkedDataModel, outputLanguage));
	}
}
