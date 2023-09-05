package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiTransformer;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TransformDebugger implements LdiTransformer {
	private final Logger log;
	private final LdiTransformer ldiTransformer;

	public TransformDebugger(LdiTransformer ldiTransformer) {
		this.ldiTransformer = ldiTransformer;
		log = LoggerFactory.getLogger(ldiTransformer.getClass());
	}

	@Override
	public List<Model> apply(Model model) {
		log.debug("Starting model: \n" + RDFWriter.source(model).lang(Lang.TTL).asString());
		return ldiTransformer.apply(model);
	}
}
