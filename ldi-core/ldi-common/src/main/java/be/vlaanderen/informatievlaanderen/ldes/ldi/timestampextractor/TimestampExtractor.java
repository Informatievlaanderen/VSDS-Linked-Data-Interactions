package be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import java.time.LocalDateTime;

public interface TimestampExtractor {

	/**
	 * @param model where from the timestamp should be extracted
	 * @return the first found timestamp in the model
	 */
	LocalDateTime extractTimestamp(Model model);

	/**
	 * @param subject where to the timestamp should belong in the statement
	 * @param model   where from the timestamp should be extracted
	 * @return first found timestamp in the model that belongs to the provided subject
	 */
	LocalDateTime extractTimestampWithSubject(Resource subject, Model model);


}
