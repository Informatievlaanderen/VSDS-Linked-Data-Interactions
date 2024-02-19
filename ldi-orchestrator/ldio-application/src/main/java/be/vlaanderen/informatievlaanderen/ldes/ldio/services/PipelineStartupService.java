package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import be.vlaanderen.informatievlaanderen.ldes.ldio.services.initializer.ConfigPipelineInitializer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.services.initializer.FileStoredPipelineInitializer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.services.initializer.PipelineInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class PipelineStartupService {
	private final Logger log = LoggerFactory.getLogger(PipelineStartupService.class);

	private final Stream<PipelineInitializer> pipelineInitiators;

	public PipelineStartupService(ConfigPipelineInitializer configPipelineInitializer,
	                              FileStoredPipelineInitializer fileStoredPipelineInitializer) {
		this.pipelineInitiators = Stream.of(fileStoredPipelineInitializer, configPipelineInitializer);
	}

	@EventListener
	public void initPipelines(ContextRefreshedEvent event) {
		pipelineInitiators.forEach(initializer -> {
			log.info("=== Processing pipelines with {} ===", initializer.name());
			var pipelines = initializer.initPipelines();
			pipelines.forEach(pipeline -> log.info("Successfully initialised pipeline: \"{}\"", pipeline.getName()));
			log.info("=== Finished {} with {} pipeline(s) initialised ===", initializer.name(), pipelines.size());
		});
	}
}
