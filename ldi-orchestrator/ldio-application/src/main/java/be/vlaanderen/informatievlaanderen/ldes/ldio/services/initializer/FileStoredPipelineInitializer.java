package be.vlaanderen.informatievlaanderen.ldes.ldio.services.initializer;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.PipelineException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.repositories.PipelineFileRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldio.services.PipelineService;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineConfigTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class FileStoredPipelineInitializer implements PipelineInitializer {
	private final Logger log = LoggerFactory.getLogger(FileStoredPipelineInitializer.class);
	private final PipelineService pipelineService;
	private final Map<File, PipelineConfigTO> storedPipelines;

	public FileStoredPipelineInitializer(PipelineService pipelineService,
	                                     PipelineFileRepository repository) {
		this.pipelineService = pipelineService;
		this.storedPipelines = repository.getInactivePipelines();
	}

	@Override
	public String name() {
		return "File Stored Config Initializer";
	}

	@Override
	public List<PipelineConfig> initPipelines() {
		return storedPipelines
				.entrySet()
				.stream()
				.map(storedPipeline -> {
					try {
						return pipelineService.addPipeline(storedPipeline.getValue().toPipelineConfig(), storedPipeline.getKey());
					} catch (PipelineException e) {
						log.error("File \"%s\": %s".formatted(storedPipeline.getKey().getName(), e.getMessage()));
						return null;
					}
				})
				.filter(Objects::nonNull)
				.toList();
	}
}
