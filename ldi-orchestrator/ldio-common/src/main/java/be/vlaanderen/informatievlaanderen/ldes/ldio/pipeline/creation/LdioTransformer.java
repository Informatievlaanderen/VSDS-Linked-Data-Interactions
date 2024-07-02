package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.ObserveConfiguration;
import io.micrometer.observation.annotation.Observed;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Ldio Wrapper that will be used to wrap any
 * {@link be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOneToOneTransformer} or
 * {@link be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOneToManyTransformer}
 */
public abstract class LdioTransformer implements LdiComponent {
	private LdioTransformer nextProcessor;
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public abstract void apply(Model model);

	@Observed
	protected void next(Model model) {
		try {
			if (nextProcessor != null) {
				nextProcessor.apply(model);
			}
		} catch (Exception e) {
			log.atError().log(ObserveConfiguration.ERROR_TEMPLATE, "transformModel", e.getMessage());
			throw e;
		}
	}

	public static LdioTransformer link(LdioTransformer first, List<LdioTransformer> chain) {
		LdioTransformer head = first;
		for (LdioTransformer nextInChain : chain) {
			head.nextProcessor = nextInChain;
			head = nextInChain;
		}
		return first;
	}

}
