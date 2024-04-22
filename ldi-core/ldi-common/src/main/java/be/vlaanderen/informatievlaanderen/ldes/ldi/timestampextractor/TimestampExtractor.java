package be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor;

import org.apache.jena.rdf.model.Model;

import java.time.LocalDateTime;

public interface TimestampExtractor {

	LocalDateTime extractTimestamp(Model model);

}
