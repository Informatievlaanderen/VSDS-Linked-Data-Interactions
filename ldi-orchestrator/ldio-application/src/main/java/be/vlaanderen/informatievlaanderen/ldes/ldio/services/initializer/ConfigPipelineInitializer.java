package be.vlaanderen.informatievlaanderen.ldes.ldio.services.initializer;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.OrchestratorConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.PipelineAlreadyExistsException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.PipelineException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.services.PipelineManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ConfigPipelineInitializer implements PipelineInitializer {
	private static final String name = "LDI Orchestrator Spring Config Initializer";
	private final Logger log = LoggerFactory.getLogger(ConfigPipelineInitializer.class);
	private final OrchestratorConfig orchestratorConfig;
	private final PipelineManagementService pipelineManagementService;

	public ConfigPipelineInitializer(OrchestratorConfig orchestratorConfig, PipelineManagementService pipelineManagementService) {
		this.orchestratorConfig = orchestratorConfig;
		this.pipelineManagementService = pipelineManagementService;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public List<PipelineConfig> initPipelines() {
		log.warn("{} DEPRECATED. Any configs with the same name will be ignored", name);
		if (orchestratorConfig.getPipelines() == null) {
			return List.of();
		}
		return orchestratorConfig.getPipelines()
				.stream()
				.map(pipeline -> {
					try {
						return pipelineManagementService.addPipeline(pipeline);
					} catch (PipelineAlreadyExistsException e) {
						log.warn(e.getMessage());
						return null;
					} catch (PipelineException e) {
						log.error(e.getMessage());
						return null;
					}
				})
				.filter(Objects::nonNull)
				.toList();
	}
}
