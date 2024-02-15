package be.vlaanderen.informatievlaanderen.ldes.ldio.services.creation;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exceptions.PipelineCreationException;
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
	private final Logger log = LoggerFactory.getLogger(FileStoredPipelineInitializer.class);
	private final PipelineManagementService pipelineManagementService;
	private final PipelineFileRepository repository;
	private final Map<File, PipelineConfigTO> pipelineFileMapping;

	public FileStoredPipelineInitializer(PipelineManagementService pipelineManagementService,
	                                     PipelineFileRepository repository) {
		this.pipelineManagementService = pipelineManagementService;
		this.repository = repository;
		this.pipelineFileMapping = repository.pipelineToFileMapping();
	}

	@Override
	public String name() {
		return "File Stored Config Initializer";
	}

	@Override
	public List<PipelineConfig> initPipelines() {
		return pipelineFileMapping
				.entrySet()
				.stream()
				.map(pipelineFileMapping -> {
					try {
						checkForRenaming(pipelineFileMapping.getKey(), pipelineFileMapping.getValue());
						return pipelineManagementService.addPipeline(pipelineFileMapping.getValue().toPipelineConfig());
					} catch (PipelineCreationException e) {
						log.error(e.getMessage());
						return null;
					} finally {
						repository.cleanupBackup(pipelineFileMapping.getKey());
					}
				})
				.filter(Objects::nonNull)
				.toList();
	}

	private void checkForRenaming(File file, PipelineConfigTO pipeline) {
		String fileName = file.getName().replace(".json", "");
		if (!fileName.equals(pipeline.name())) {
			log.warn("Filename {} was not in line with its pipeline name {}. This will be renamed", fileName, pipeline.name());
			repository.backup(file);
		}
	}
}
