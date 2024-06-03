package be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import java.time.LocalDateTime;

/**
 * Default or empty implementation of the TimestampExtractor
 */
public class TimestampFromCurrentTimeExtractor implements TimestampExtractor {
	@Override
	public LocalDateTime extractTimestamp(Model model) {
		return LocalDateTime.now();
	}

	@Override
	public LocalDateTime extractTimestampWithSubject(Resource subject, Model model) {
		return LocalDateTime.now();
	}
}
