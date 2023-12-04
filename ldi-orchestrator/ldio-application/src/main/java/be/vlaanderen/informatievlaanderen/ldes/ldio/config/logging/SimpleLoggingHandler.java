package be.vlaanderen.informatievlaanderen.ldes.ldio.config.logging;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class SimpleLoggingHandler implements ObservationHandler<Observation.Context> {
	private static final Logger log = LoggerFactory.getLogger(SimpleLoggingHandler.class);

	@Override
	public boolean supportsContext(Observation.Context context) {
		return true;
	}

	@Override
	public void onError(Observation.Context context) {
		log.atError().log(() -> getErrorInfo(context));
	}

	private String getErrorInfo(Observation.Context context) {
		String problem = Objects.requireNonNull(context.getError()).getMessage();
		String source = context.getName();
		String when = Objects.requireNonNull(context.getContextualName()).split("#")[1];
		return "ERROR - problem='%s', source='%s', when='%s'".formatted(problem, source, when);
	}
}
