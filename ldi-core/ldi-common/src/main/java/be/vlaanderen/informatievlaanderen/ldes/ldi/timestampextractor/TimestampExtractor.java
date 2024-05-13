package be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import java.time.LocalDateTime;

public interface TimestampExtractor {

	LocalDateTime extractTimestamp(Model model);
	LocalDateTime extractTimestampWithSubject(Resource subject, Model model);


}
