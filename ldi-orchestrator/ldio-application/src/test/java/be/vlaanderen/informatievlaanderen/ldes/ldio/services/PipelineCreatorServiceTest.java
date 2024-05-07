package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.OrchestratorConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.LdiAdapterMissingException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.modules.DummyIn;
import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.PipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.StartedPipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentDefinitionTO;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.InputComponentDefinitionTO;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineConfigTO;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.ldio")
@Import(value = PipelineCreatorServiceTest.NonAdapterRequiredInputConfig.class)
class PipelineCreatorServiceTest {
	private static final String PIPELINE_NAME = "pipeline-name";
	@Autowired
	private OrchestratorConfig orchestratorConfig;
	@Autowired
	private ConfigurableApplicationContext configurableApplicationContext;
	@Autowired
	private ApplicationEventPublisher eventPublisher;
	private PipelineCreatorService pipelineCreatorService;

	@BeforeEach
	void setUp() {
		pipelineCreatorService = new PipelineCreatorService(orchestratorConfig, configurableApplicationContext, eventPublisher, null);
	}

	@Test
	void given_InputConfigWithoutAdapter_when_InitialisePipeline_then_ThrowException() {
		final InputComponentDefinitionTO inputDefinitionTO = new InputComponentDefinitionTO("dummyIn", null, Map.of());
		final PipelineConfigTO pipelineConfigTO = new PipelineConfigTO(PIPELINE_NAME, "", inputDefinitionTO, List.of(), List.of());
		final PipelineConfig pipelineConfig = pipelineConfigTO.toPipelineConfig();

		assertThatThrownBy(() -> pipelineCreatorService.initialisePipeline(pipelineConfig))
				.isInstanceOf(LdiAdapterMissingException.class)
				.hasMessage("Pipeline \"%s\": Input: \"%s\": Missing LDI Adapter", PIPELINE_NAME, "dummyIn");
	}

	@Test
	void given_InputConfigWithAdapter_when_InitialisePipeline_then_ThrowNoException() {
		final ComponentDefinitionTO adapterTO = new ComponentDefinitionTO("dummyAdapt", Map.of());
		final InputComponentDefinitionTO inputDefinitionTO = new InputComponentDefinitionTO("dummyIn", adapterTO, Map.of());
		final PipelineConfigTO pipelineConfigTO = new PipelineConfigTO(PIPELINE_NAME, "", inputDefinitionTO, List.of(), List.of());
		final PipelineConfig pipelineConfig = pipelineConfigTO.toPipelineConfig();

		assertThatNoException().isThrownBy(() -> pipelineCreatorService.initialisePipeline(pipelineConfig));
	}

	@Test
	void given_InputConfigWithoutAdapter_when_InitialisePipeline_then_ThrowNoException() {
		final ComponentDefinitionTO adapterTO = new ComponentDefinitionTO("dummyAdapt", Map.of());
		final InputComponentDefinitionTO inputDefinitionTO = new InputComponentDefinitionTO("dummyNonAdapterRequiredIn", adapterTO, Map.of());
		final PipelineConfigTO pipelineConfigTO = new PipelineConfigTO(PIPELINE_NAME, "", inputDefinitionTO, List.of(), List.of());
		final PipelineConfig pipelineConfig = pipelineConfigTO.toPipelineConfig();

		assertThatNoException().isThrownBy(() -> pipelineCreatorService.initialisePipeline(pipelineConfig));
	}

	@TestConfiguration
	static class NonAdapterRequiredInputConfig {
		@Bean
		@Qualifier("dummyNonAdapterRequiredIn")
		public LdioInputConfigurator dummyNonAdapterRequiredIn() {
			return new LdioInputConfigurator() {
				@Override
				public LdioInput configure(LdiAdapter adapter, ComponentExecutor executor, ComponentProperties properties) {
					return new DummyIn(executor, content -> Stream.of(ModelFactory.createDefaultModel()));
				}

				@Override
				public boolean isAdapterRequired() {
					return false;
				}

				@Override
				public PipelineStatus getInitialPipelineStatus() {
					return new StartedPipelineStatus();
				}
			};
		}
	}

}