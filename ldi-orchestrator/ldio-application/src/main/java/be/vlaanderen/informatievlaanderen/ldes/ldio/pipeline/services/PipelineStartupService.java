package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.services;

import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.services.initializer.ConfigPipelineInitializer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.services.initializer.FileStoredPipelineInitializer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.services.initializer.PipelineInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.ApplicationModuleInitializer;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class PipelineStartupService implements ApplicationModuleInitializer {
	private final Logger log = LoggerFactory.getLogger(PipelineStartupService.class);

	private final Stream<PipelineInitializer> pipelineInitiators;

	public PipelineStartupService(ConfigPipelineInitializer configPipelineInitializer,
	                              FileStoredPipelineInitializer fileStoredPipelineInitializer) {
		this.pipelineInitiators = Stream.of(fileStoredPipelineInitializer, configPipelineInitializer);
	}

	@Override
	public void initialize() {
		pipelineInitiators.forEach(initializer -> {
			log.info("=== Processing pipelines with {} ===", initializer.name());
			var pipelines = initializer.initPipelines();
			pipelines.forEach(pipeline -> log.info("Successfully initialised pipeline: \"{}\"", pipeline.getName()));
			log.info("=== Finished {} with {} pipeline(s) initialised ===", initializer.name(), pipelines.size());
		});
	}
}
