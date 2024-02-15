package be.vlaanderen.informatievlaanderen.ldes.ldio.services.creation;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.OrchestratorConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exceptions.PipelineCreationException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.services.PipelineManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ConfigPipelineInitializer implements PipelineInitializer {
	private final Logger log = LoggerFactory.getLogger(ConfigPipelineInitializer.class);
	private final OrchestratorConfig orchestratorConfig;
	private final PipelineManagementService pipelineManagementService;

	public ConfigPipelineInitializer(OrchestratorConfig orchestratorConfig, PipelineManagementService pipelineManagementService) {
		this.orchestratorConfig = orchestratorConfig;
		this.pipelineManagementService = pipelineManagementService;
	}

	@Override
	public String name() {
		return "LDI Orchestrator Config Initializer";
	}

	@Override
	public List<PipelineConfig> initPipelines() {
		log.warn("Spring config is second priority to ");
		return orchestratorConfig.getPipelines()
				.stream()
				.map(pipeline -> {
					try {
						return pipelineManagementService.addPipeline(pipeline);
					} catch (PipelineCreationException e) {
						log.error(e.getMessage());
						return null;
					}
				})
				.filter(Objects::nonNull)
				.toList();
	}
}
