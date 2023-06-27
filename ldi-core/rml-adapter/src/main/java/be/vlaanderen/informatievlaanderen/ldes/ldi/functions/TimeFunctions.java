package be.vlaanderen.informatievlaanderen.ldes.ldi.functions;

import io.carml.engine.function.FnoFunction;
import io.carml.engine.function.FnoParam;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatterBuilder;

public class TimeFunctions {
	@FnoFunction(LDI.EPOCH_TO_ISO8601_FUNCTION)
	public String replaceFunction(@FnoParam(LDI.EPOCH) Long epoch) {

		return ZonedDateTime.ofInstant(Instant.ofEpochMilli(epoch * 1000), ZoneId.systemDefault())
				.format(new DateTimeFormatterBuilder().appendInstant(3).toFormatter());
	}
}
