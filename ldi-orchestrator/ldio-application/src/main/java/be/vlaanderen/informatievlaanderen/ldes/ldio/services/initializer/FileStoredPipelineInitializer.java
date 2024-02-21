package be.vlaanderen.informatievlaanderen.ldes.ldio.services.initializer;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.PipelineException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.repositories.PipelineFileRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldio.services.PipelineManagementService;
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
	private final String NAME = "File Stored Config Initializer";
	private final Logger log = LoggerFactory.getLogger(FileStoredPipelineInitializer.class);
	private final PipelineManagementService pipelineManagementService;
	private final Map<File, PipelineConfigTO> pipelineFileMappings;

	public FileStoredPipelineInitializer(PipelineManagementService pipelineManagementService,
	                                     PipelineFileRepository repository) {
		this.pipelineManagementService = pipelineManagementService;
		this.pipelineFileMappings = repository.pipelineToFileMapping();
	}

	@Override
	public String name() {
		return NAME;
	}

	@Override
	public List<PipelineConfig> initPipelines() {
		return pipelineFileMappings
				.entrySet()
				.stream()
				.map(pipelineFileMapping -> {
					try {
						return pipelineManagementService.addPipeline(pipelineFileMapping.getValue().toPipelineConfig(), pipelineFileMapping.getKey());
					} catch (PipelineException e) {
						log.error("File \"%s\": %s".formatted(pipelineFileMapping.getKey().getName(), e.getMessage()));
						return null;
					}
				})
				.filter(Objects::nonNull)
				.toList();
	}
}
