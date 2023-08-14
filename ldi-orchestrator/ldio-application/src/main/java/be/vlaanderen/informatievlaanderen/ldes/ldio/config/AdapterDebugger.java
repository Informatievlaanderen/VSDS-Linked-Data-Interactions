package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

public class AdapterDebugger implements LdiAdapter {
	private final Logger logger;
	private final LdiAdapter adapter;

	public AdapterDebugger(LdiAdapter adapter) {
		this.logger = LoggerFactory.getLogger(adapter.getClass());
		this.adapter = adapter;
	}

	@Override
	public Stream<Model> apply(Content content) {
		if (logger.isDebugEnabled()) {
			logger.debug("Starting point: " + content);
		}

		return adapter.apply(content);
	}
}
