package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import io.micrometer.observation.aop.ObservedAspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
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
//		var joinPoint = context.getProceedingJoinPoint();
		String problem = Objects.requireNonNull(context.getError()).getMessage();
		String source = context.getName();
		String when = Objects.requireNonNull(context.getContextualName()).split("#")[1];
		return "ERROR - problem='%s', source='%s', when='%s'".formatted(problem, source, when);
	}
}
