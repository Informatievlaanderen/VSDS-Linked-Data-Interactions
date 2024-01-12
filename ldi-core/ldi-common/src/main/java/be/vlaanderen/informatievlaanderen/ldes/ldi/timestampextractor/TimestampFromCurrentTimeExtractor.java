package be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor;

import org.apache.jena.rdf.model.Model;

import java.time.LocalDateTime;

public class TimestampFromCurrentTimeExtractor implements TimestampExtractor {
	@Override
	public LocalDateTime extractTimestamp(Model model) {
		return LocalDateTime.now();
	}
}
