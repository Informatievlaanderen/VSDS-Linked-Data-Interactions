package be.vlaanderen.informatievlaanderen.ldes.ldio.services.initializer;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.OrchestratorConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.PipelineAlreadyExistsException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.PipelineException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.services.PipelineService;
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
	private final PipelineService pipelineService;

	public ConfigPipelineInitializer(OrchestratorConfig orchestratorConfig, PipelineService pipelineService) {
		this.orchestratorConfig = orchestratorConfig;
		this.pipelineService = pipelineService;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public List<PipelineConfig> initPipelines() {
		if (orchestratorConfig.getPipelines() == null) {
			return List.of();
		}
		log.warn("{} DEPRECATED. Any configs with the same name will be ignored", name);
		return orchestratorConfig.getPipelines()
				.stream()
				.map(pipeline -> {
					try {
						return pipelineService.addPipeline(pipeline);
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
