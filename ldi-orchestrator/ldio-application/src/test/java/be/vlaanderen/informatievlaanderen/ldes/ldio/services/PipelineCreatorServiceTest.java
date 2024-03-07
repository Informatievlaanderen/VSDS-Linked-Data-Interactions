package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.OrchestratorConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.LdiAdapterMissingException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentDefinitionTO;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.InputComponentDefinitionTO;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineConfigTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
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
		final InputComponentDefinitionTO inputDefinitionTO = new InputComponentDefinitionTO("Ldio:HttpIn", null, Map.of());
		final PipelineConfigTO pipelineConfigTO = new PipelineConfigTO(PIPELINE_NAME, "", inputDefinitionTO, List.of(), List.of());
		final PipelineConfig pipelineConfig = pipelineConfigTO.toPipelineConfig();

		assertThatThrownBy(() -> pipelineCreatorService.initialisePipeline(pipelineConfig))
				.isInstanceOf(LdiAdapterMissingException.class)
				.hasMessage("Pipeline \"%s\": Input: \"%s\": Missing LDI Adapter", PIPELINE_NAME, "Ldio:HttpIn");
	}

	@Test
	void given_InputConfigWithAdapter_when_InitialisePipeline_then_ThrowNoException() {
		final ComponentDefinitionTO adapterTO = new ComponentDefinitionTO("Ldio:RdfAdapter", Map.of());
		final InputComponentDefinitionTO inputDefinitionTO = new InputComponentDefinitionTO("Ldio:HttpIn", adapterTO, Map.of());
		final PipelineConfigTO pipelineConfigTO = new PipelineConfigTO(PIPELINE_NAME, "", inputDefinitionTO, List.of(), List.of());
		final PipelineConfig pipelineConfig = pipelineConfigTO.toPipelineConfig();

		assertThatNoException().isThrownBy(() -> pipelineCreatorService.initialisePipeline(pipelineConfig));
	}

}