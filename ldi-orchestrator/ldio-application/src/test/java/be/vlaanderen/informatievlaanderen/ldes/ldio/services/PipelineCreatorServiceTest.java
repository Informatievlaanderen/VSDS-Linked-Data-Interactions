package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import be.vlaanderen.informatievlaanderen.ldes.ldio.OrchestratorConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.PipelineConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.dto.ComponentDefinitionTO;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.dto.InputComponentDefinitionTO;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.dto.PipelineConfigTO;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.exception.LdiAdapterMissingException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.services.PipelineCreatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.ldio")
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

}