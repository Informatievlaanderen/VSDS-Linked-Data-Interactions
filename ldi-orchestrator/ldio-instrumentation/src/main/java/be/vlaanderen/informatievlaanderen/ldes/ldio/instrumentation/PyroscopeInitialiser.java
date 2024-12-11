package be.vlaanderen.informatievlaanderen.ldes.ldio.instrumentation;

import io.pyroscope.javaagent.PyroscopeAgent;
import io.pyroscope.javaagent.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "pyroscope.agent.enabled", havingValue = "true")
public class PyroscopeInitialiser {

	@EventListener(ApplicationReadyEvent.class)
	public void init() {
		PyroscopeAgent.start(Config.build());
	}
}