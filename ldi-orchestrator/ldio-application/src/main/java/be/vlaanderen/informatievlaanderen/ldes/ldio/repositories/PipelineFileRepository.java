package be.vlaanderen.informatievlaanderen.ldes.ldio.repositories;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.OrchestratorConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.PipelineAlreadyExistsException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.PipelineDoesNotExistsException;
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
	private final File directory;
	private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
	private final ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
	private final ObjectReader reader = mapper.readerFor(PipelineConfig.class);

	public PipelineFileRepository(OrchestratorConfig config) {
		activePipelines = new HashMap<>();
		directory = new File(config.getDirectory());
		directory.mkdirs();
	}

	@Override
	public List<PipelineConfigTO> findAll() {
		return activePipelines.values().stream().map(SavedPipeline::pipelineConfig).toList();
	}

	public Map<File, PipelineConfigTO> getStoredPipelines() {
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
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}


	@Override
	public void save(PipelineConfig pipeline) {
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

	@Override
	public void save(PipelineConfig pipeline, File persistedFile) {
		if (exists(pipeline.getName())) {
			throw new PipelineAlreadyExistsException(pipeline.getName());
		}
		activePipelines.put(pipeline.getName(), new SavedPipeline(fromPipelineConfig(pipeline), persistedFile));
	}


	@Override
	public void delete(String pipelineName) {
		ofNullable(activePipelines.get(pipelineName))
				.map(savedPipeline -> {
					activePipelines.remove(pipelineName);
					try {
						Files.delete(savedPipeline.file.toPath());
						return true;
					} catch (IOException e) {
						return false;
					}
				}).ifPresentOrElse(ignored -> log.info("Pipeline {} was successfully removed and deleted", pipelineName), () -> {
					throw new PipelineDoesNotExistsException(pipelineName);
				});
	}

	public boolean exists(String pipeline) {
		return activePipelines.containsKey(pipeline);
	}

	protected PipelineConfigTO readConfigFile(Path path) throws PipelineParsingException {
		try (Stream<String> content = Files.lines(path)) {
			var json = content.collect(Collectors.joining("\n"));
			return reader.readValue(json, PipelineConfigTO.class);
		} catch (IOException e) {
			throw new PipelineParsingException(path.getFileName().toString());
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
