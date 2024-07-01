package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.modules.*;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.components.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.components.LdioTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.configurator.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.configurator.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.configurator.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.configurator.LdioTransformerConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockFlowConfiguration {
	@Bean
	@Qualifier("dummyIn")
	public LdioInputConfigurator dummyIn() {
		return new DummyInConfigurator();
	}

	@Bean
	@Qualifier("dummyAdapt")
	public LdioConfigurator dummyAdapt() {
		return new DummyAdaptConfigurator();
	}

	@Bean
	@Qualifier("dummyTransform")
	public LdioTransformerConfigurator dummyTransform() {
		return new DummyTransformTransformerConfigurator();
	}

	@Bean
	@Qualifier("dummyOut")
	public LdioConfigurator dummyOut() {
		return new DummyOutProcessorConfigurator();
	}

	static class DummyInConfigurator implements LdioInputConfigurator {

		@Override
		public LdioInput configure(LdiAdapter adapter,
		                           ComponentExecutor executor,
								   ApplicationEventPublisher applicationEventPublisher,
		                           ComponentProperties config) {
			return new DummyIn(executor, adapter, applicationEventPublisher);
		}

		@Override
		public boolean isAdapterRequired() {
			return true;
		}
	}

	static class DummyAdaptConfigurator implements LdioConfigurator {
		@Override
		public LdiAdapter configure(ComponentProperties properties) {
			return new DummyAdapt();
		}
	}

	static class DummyTransformTransformerConfigurator implements LdioTransformerConfigurator {

		@Override
		public LdioTransformer configure(ComponentProperties properties) {
			return new DummyTransform();
		}
	}

	static class DummyOutProcessorConfigurator implements LdioConfigurator {
		@Autowired
		MockVault mockVault;

		@Override
		public LdiOutput configure(ComponentProperties properties) {
			return new DummyOut(mockVault);
		}
	}

}
