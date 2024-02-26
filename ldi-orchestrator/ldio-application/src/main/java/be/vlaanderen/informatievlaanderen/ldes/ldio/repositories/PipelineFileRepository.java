package be.vlaanderen.informatievlaanderen.ldes.ldio.repositories;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.OrchestratorConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.PipelineAlreadyExistsException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.PipelineParsingException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineConfigTO;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineConfigTO.fromPipelineConfig;
import static java.util.Optional.ofNullable;

@Service
public class PipelineFileRepository implements PipelineRepository {
	public static final String EXTENSION_YML = ".yml";
	public static final String EXTENSION_YAML = ".yaml";
	private final Logger log = LoggerFactory.getLogger(PipelineFileRepository.class);
	private final Map<String, SavedPipeline> activePipelines;
	private boolean persistenceEnabled;
	private File directory;
	private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
	private final ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
	private final ObjectReader reader = mapper.readerFor(PipelineConfig.class);

	public PipelineFileRepository(OrchestratorConfig config) {
		activePipelines = new HashMap<>();
		if (config.getDirectory() != null) {
			persistenceEnabled = true;
			directory = new File(config.getDirectory());
			directory.mkdirs();
		}
	}

	@Override
	public List<PipelineConfigTO> getActivePipelines() {
		return activePipelines.values().stream().map(SavedPipeline::pipelineConfig).toList();
	}

	/**
	 * Retrieves a map of inactive pipelines.
	 *
	 * @return A map where the keys are File objects representing the persisted files of the pipelines,
	 * and the values are PipelineConfigTO objects representing the pipeline configurations.
	 */
	public Map<File, PipelineConfigTO> getInactivePipelines() {
		if (persistenceEnabled) {
			try (Stream<Path> files = Files.list(directory.toPath())) {
				return files
						.filter(path -> !Files.isDirectory(path))
						.filter(path -> path.toFile().getName().endsWith(EXTENSION_YML)
						                || path.toFile().getName().endsWith(EXTENSION_YAML))
						.map(path -> {
							try {
								return Map.of(path.toFile(), readConfigFile(path));
							} catch (PipelineParsingException e) {
								log.error(e.getMessage());
								return null;
							}
						})
						.filter(Objects::nonNull)
						.map(Map::entrySet)
						.flatMap(Collection::stream)
						.filter(pipelineConfigTOEntry -> !exists(pipelineConfigTOEntry.getValue().name()))
						.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		} else {
			return Map.of();
		}

	}

	@Override
	public void activateNewPipeline(PipelineConfig pipeline) {
		if (persistenceEnabled) {
			persistPipelineToFile(pipeline);
		} else {
			if (exists(pipeline.getName())) {
				throw new PipelineAlreadyExistsException(pipeline.getName());
			}
			activePipelines.put(pipeline.getName(), new SavedPipeline(fromPipelineConfig(pipeline), null));
		}

	}

	@Override
	public void activateExistingPipeline(PipelineConfig pipeline, File persistedFile) {
		if (exists(pipeline.getName())) {
			throw new PipelineAlreadyExistsException(pipeline.getName());
		}
		activePipelines.put(pipeline.getName(), new SavedPipeline(fromPipelineConfig(pipeline), persistedFile));
	}


	/**
	 * Deletes a pipeline given its name.
	 *
	 * @param pipelineName The name of the pipeline to be deleted.
	 * @return A boolean indicating whether the deletion was successful.
	 *         Returns true if the pipeline was found and successfully deleted,
	 *         false if the pipeline was not found or could not be deleted due to an IOException.
	 */
	@Override
	public boolean delete(String pipelineName) {
		return ofNullable(activePipelines.get(pipelineName))
				.map(savedPipeline -> {
					activePipelines.remove(pipelineName);
					if (persistenceEnabled) {
						try {
							Files.delete(savedPipeline.file.toPath());
							return true;
						} catch (IOException e) {
							return false;
						}
					}
					return true;
				}).orElse(false);
	}

	@Override
	public boolean exists(String pipeline) {
		return activePipelines.containsKey(pipeline);
	}

	protected PipelineConfigTO readConfigFile(Path path) throws PipelineParsingException {
		try {
			return reader.readValue(path.toFile(), PipelineConfigTO.class);
		} catch (IOException e) {
			throw new PipelineParsingException(path.getFileName().toString());
		}
	}

	private void persistPipelineToFile(PipelineConfig pipeline) {
		if (exists(pipeline.getName())) {
			throw new PipelineAlreadyExistsException(pipeline.getName());
		}
		var pipelineConfig = fromPipelineConfig(pipeline);
		var savedFile = pipelineFile(pipeline.getName());

		if (savedFile.exists()) {
			AtomicInteger count = new AtomicInteger();
			boolean success = false;
			do {
				savedFile = new File(directory, savedFile.getName().replace(EXTENSION_YML, ("(%d)%s").formatted(count.incrementAndGet(), EXTENSION_YML)));
				if (!savedFile.exists()) {
					success = true;
				}
			} while (!success);
		}

		try {
			writer.writeValue(savedFile, pipelineConfig);
			activePipelines.put(pipeline.getName(), new SavedPipeline(pipelineConfig, savedFile));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private String fileName(String pipeline) {
		return "%s%s".formatted(pipeline, EXTENSION_YML);
	}

	private File pipelineFile(String pipeline) {
		return new File(directory, fileName(pipeline));
	}

	private record SavedPipeline(PipelineConfigTO pipelineConfig, File file) {
	}
}
