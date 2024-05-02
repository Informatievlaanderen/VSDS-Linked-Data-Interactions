package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioTransformerConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.modules.*;
import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.PipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.StartedPipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
		                           ComponentProperties config) {
			return new DummyIn(executor, adapter);
		}

		@Override
		public boolean isAdapterRequired() {
			return true;
		}

		@Override
		public PipelineStatus getInitialPipelineStatus() {
			return new StartedPipelineStatus();
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
