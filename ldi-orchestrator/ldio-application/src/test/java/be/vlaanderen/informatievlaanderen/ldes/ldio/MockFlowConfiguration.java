package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.modules.*;
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
	public LdioConfigurator dummyTransform() {
		return new DummyTransformConfigurator();
	}

	@Bean
	@Qualifier("dummyOut")
	public LdioConfigurator dummyOut() {
		return new DummyOutConfigurator();
	}

	static class DummyInConfigurator implements LdioInputConfigurator {

		@Override
		public LdiInput configure(LdiAdapter adapter,
				ComponentExecutor executor,
				ComponentProperties config) {
			return new DummyIn(executor, adapter);
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
		@Autowired
		MockVault mockVault;

		@Override
		public LdiComponent configure(ComponentProperties properties) {
			return new DummyOut(mockVault);
		}
	}
}
