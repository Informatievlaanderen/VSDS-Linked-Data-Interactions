package be.vlaanderen.informatievlaanderen.ldes.ldio;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.impl.LiteralImpl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;

public class LocalDateTimeConverter {

	public LocalDateTime getLocalDateTime(Literal timestamp) {
		RDFDatatype datatype = timestamp.getDatatype();
		XSDDateTime parse = (XSDDateTime) datatype.parse(timestamp.getValue().toString());
		Calendar calendar = parse.asCalendar();
		TimeZone tz = calendar.getTimeZone();
		ZoneId zoneId = tz.toZoneId();
		return LocalDateTime.ofInstant(calendar.toInstant(), zoneId);
	}

}
