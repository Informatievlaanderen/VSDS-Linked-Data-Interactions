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

import static be.vlaanderen.informatievlaanderen.ldes.ldio.repositories.PipelineFileRepository.EXTENSION;

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
					boolean needsRestoring = false;
					try {
						needsRestoring = checkForRenaming(pipelineFileMapping.getKey(), pipelineFileMapping.getValue());
						var pipeline = pipelineManagementService.addPipeline(pipelineFileMapping.getValue().toPipelineConfig(), needsRestoring);
						repository.cleanupBackup(pipelineFileMapping.getKey());
						return pipeline;
					} catch (PipelineException e) {
						if (needsRestoring) {
							repository.backup(pipelineFileMapping.getKey());
						}
						log.error("File \"%s\": %s".formatted(pipelineFileMapping.getKey().getName(), e.getMessage()));
						return null;
					}
				})
				.filter(Objects::nonNull)
				.toList();
	}

	private boolean checkForRenaming(File file, PipelineConfigTO pipeline) {
		String fileName = file.getName().replace(EXTENSION, "");
		if (!fileName.equals(pipeline.name())) {
			log.warn("File \"{}\" was not in line with its pipeline name \"{}\". This will be renamed", file.getName(), pipeline.name());
			repository.backup(file);
			return false;
		}
		return true;
	}
}
