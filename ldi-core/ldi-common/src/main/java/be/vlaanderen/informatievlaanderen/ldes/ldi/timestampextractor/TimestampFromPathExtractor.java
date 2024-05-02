package be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.rdf.model.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;

public class TimestampFromPathExtractor implements TimestampExtractor {

	private final Property timestampPath;

	public TimestampFromPathExtractor(Property timestampPath) {
		this.timestampPath = timestampPath;
	}

	@Override
	public LocalDateTime extractTimestamp(Model model) {
		final NodeIterator timestampNodeIterator = model.listObjectsOfProperty(timestampPath);
		final Literal timestampLiteral = extractTimestampLiteral(timestampNodeIterator);
		return getLocalDateTime(timestampLiteral);
	}

	@Override
	public LocalDateTime extractTimestampWithSubject(Resource subject, Model model) {
		final NodeIterator timestampNodeIterator = model.listObjectsOfProperty(subject, timestampPath);
		final Literal timestampLiteral = extractTimestampLiteral(timestampNodeIterator);
		return getLocalDateTime(timestampLiteral);
	}

	private Literal extractTimestampLiteral(NodeIterator nodeIterator) {
		return nodeIterator
				.filterDrop(node -> !node.isLiteral())
				.mapWith(RDFNode::asLiteral)
				.nextOptional()
				.orElseThrow(() -> new IllegalArgumentException("No timestamp as literal found on member"));
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
