package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.modules.DummyAdapt;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.modules.DummyIn;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.modules.DummyOut;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.modules.DummyTransform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockFlowConfiguration {
	@Bean
	@Qualifier("dummyIn")
	public LdioConfigurator dummyIn(ComponentExecutor executor, LdiAdapter adapter) {
		return new DummyInConfigurator(adapter, executor);
	}

	@Bean
	@Qualifier("dummyAdapt")
	public LdioConfigurator dummyAdapt() {
		return new DummyAdaptConfigurator();
	}

	@Bean
	@Qualifier("dummyTransform")
	public LdioConfigurator dummyTransform() {
		return new DummyTransformConfigurator();
	}

	@Bean
	@Qualifier("dummyOut")
	public LdioConfigurator dummyOut() {
		return new DummyOutConfigurator();
	}

	static class DummyInConfigurator implements LdioConfigurator {
		ComponentExecutor executor;
		LdiAdapter adapter;

		public DummyInConfigurator(LdiAdapter adapter, ComponentExecutor executor) {
			this.executor = executor;
			this.adapter = adapter;
		}

		@Override
		public LdiComponent configure(ComponentProperties properties) {
			return new DummyIn()
					.withAdapter(adapter)
					.withExecutor(executor);
		}
	}

	static class DummyAdaptConfigurator implements LdioConfigurator {
		@Override
		public LdiComponent configure(ComponentProperties properties) {
			return new DummyAdapt();
		}
	}

	static class DummyTransformConfigurator implements LdioConfigurator {
		@Override
		public LdiComponent configure(ComponentProperties properties) {
			return new DummyTransform();
		}
	}

	static class DummyOutConfigurator implements LdioConfigurator {
		@Override
		public LdiComponent configure(ComponentProperties properties) {
			return new DummyOut();
		}
	}
}
