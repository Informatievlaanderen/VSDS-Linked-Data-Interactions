package be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;

public class TimestampFromPathExtractor implements TimestampExtractor {

	private final Property timestampPath;

	public TimestampFromPathExtractor(Property timestampPath) {
		this.timestampPath = timestampPath;
	}

	public LocalDateTime extractTimestamp(Model model) {
		var timestamp = model
				.listObjectsOfProperty(timestampPath)
				.filterDrop(node -> !node.isLiteral())
				.mapWith(RDFNode::asLiteral)
				.nextOptional()
				.orElseThrow(() -> new IllegalArgumentException("No timestamp as literal found on member"));

		return getLocalDateTime(timestamp);
	}

	private LocalDateTime getLocalDateTime(Literal timestamp) {
		RDFDatatype datatype = timestamp.getDatatype();
		XSDDateTime parse = (XSDDateTime) datatype.parse(timestamp.getValue().toString());
		Calendar calendar = parse.asCalendar();
		TimeZone tz = calendar.getTimeZone();
		ZoneId zoneId = tz.toZoneId();
		return LocalDateTime.ofInstant(calendar.toInstant(), zoneId);
	}

}
