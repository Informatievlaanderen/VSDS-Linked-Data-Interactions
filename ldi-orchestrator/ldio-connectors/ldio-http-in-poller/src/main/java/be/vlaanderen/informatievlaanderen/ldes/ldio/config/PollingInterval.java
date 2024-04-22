package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.scheduling.support.CronTrigger;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioHttpInputPollerProperties.*;

public class PollingInterval {
	private static final Logger logger = LoggerFactory.getLogger(PollingInterval.class);
	private CronTrigger cronTrigger;
	private Duration duration;

	public PollingInterval(CronTrigger cronTrigger) {
		this.cronTrigger = cronTrigger;
	}

	public PollingInterval(Duration duration) {
		this.duration = duration;
	}

	public static PollingInterval withCron(String cronExpression) {
		if (!CronExpression.isValidExpression(cronExpression)) {
			throw new IllegalArgumentException(INVALID_PROPERTY + CRON);
		}
		return new PollingInterval(new CronTrigger(cronExpression));
	}

	public static PollingInterval withInterval(String interval) {
		logger.warn(INTERVAL_MIGRATION_WARNING);

		try {
			return new PollingInterval(Duration.parse(interval));
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException(INVALID_PROPERTY.formatted(INTERVAL, interval));
		}
	}

	public TYPE getType() {
		return cronTrigger != null ? TYPE.CRON : TYPE.INTERVAL;
	}

	public Duration getDuration() {
		return duration;
	}

	public CronTrigger getCronTrigger() {
		return cronTrigger;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PollingInterval that = (PollingInterval) o;
		return Objects.equals(cronTrigger, that.cronTrigger) && Objects.equals(duration, that.duration);
	}

	@Override
	public int hashCode() {
		return Objects.hash(cronTrigger, duration);
	}

	public enum TYPE {CRON, INTERVAL}
}
