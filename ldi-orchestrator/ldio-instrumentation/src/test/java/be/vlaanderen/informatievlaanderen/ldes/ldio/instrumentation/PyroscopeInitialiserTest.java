package be.vlaanderen.informatievlaanderen.ldes.ldio.instrumentation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class PyroscopeInitialiserTest {
	private ApplicationContextRunner runner;

	@BeforeEach
	void setUp() {
		runner = new ApplicationContextRunner()
				.withUserConfiguration(PyroscopeInitialiser.class);
	}

	@Test
	void given_EnabledIsTrue_when_RunContext_then_BeanIsPresent() {
		runner
				.withPropertyValues("pyroscope.agent.enabled=true")
				.run(context -> assertThat(context).hasSingleBean(PyroscopeInitialiser.class));
	}

	@Test
	void given_EnabledIsFalse_when_RunContext_then_BeanIsAbsent() {
		runner
				.withPropertyValues("pyroscope.agent.enabled=false")
				.run(context -> assertThat(context).doesNotHaveBean(PyroscopeInitialiser.class));
	}

	@Test
	void given_MissingEnabledProperty_when_RunContext_then_BeanIsAbsent() {
		runner.run(context -> assertThat(context).doesNotHaveBean(PyroscopeInitialiser.class));
	}
}