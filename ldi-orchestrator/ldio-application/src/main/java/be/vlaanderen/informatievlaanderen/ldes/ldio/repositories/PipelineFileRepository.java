package be.vlaanderen.informatievlaanderen.ldes.ldio.repositories;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.OrchestratorConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.PipelineDoesNotExistsException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineConfigTO;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineConfigTO.fromPipelineConfig;
import static java.util.Optional.ofNullable;

@Service
public class PipelineFileRepository implements PipelineRepository {
	public static final String EXTENSION_YML = ".yml";
	public static final String EXTENSION_YAML = ".yaml";
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

	public Map<File, PipelineConfigTO> pipelineToFileMapping() {
		try (Stream<Path> files = Files.list(directory.toPath())) {
			return files
					.filter(path -> !Files.isDirectory(path))
					.filter(path -> path.toFile().getName().endsWith(EXTENSION_YML)
							|| path.toFile().getName().endsWith(EXTENSION_YAML))
					.collect(Collectors.toMap(Path::toFile, path -> {
						try (Stream<String> content = Files.lines(path)) {
							var json = content.collect(Collectors.joining("\n"));
							return reader.readValue(json, PipelineConfigTO.class);
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	@Override
	public void save(PipelineConfig pipeline) {
		var pipelineConfig = fromPipelineConfig(pipeline);
		var savedFile = pipelineFile(pipeline.getName());
		try {
			writer.writeValue(savedFile, fromPipelineConfig(pipeline));
		} catch (IOException e) {
			AtomicInteger count = new AtomicInteger();
			boolean success = false;
			do {
				try {
					savedFile = new File(savedFile.getName().replace(EXTENSION_YML, ("(%d)%s").formatted(count.incrementAndGet(), EXTENSION_YML)));
					writer.writeValue(savedFile, pipelineConfig);
					success = true;
				} catch (IOException ignored) {
				}
			} while (!success);
		}

		activePipelines.put(pipeline.getName(), new SavedPipeline(pipelineConfig, savedFile));
	}

	@Override
	public void save(PipelineConfig pipeline, File persistedFile) {
		activePipelines.put(pipeline.getName(), new SavedPipeline(fromPipelineConfig(pipeline), persistedFile));
	}


	@Override
	public void delete(String pipelineName) {
		var deleteResult = ofNullable(activePipelines.get(pipelineName))
				.map(savedPipeline -> {
					activePipelines.remove(pipelineName);
					return savedPipeline.file().delete();
				}).orElse(false);

		if (!deleteResult) {
			throw new PipelineDoesNotExistsException(pipelineName);
		}
	}

	public boolean exists(String pipeline) {
		return activePipelines.containsKey(pipeline);
	}

	public String fileName(String pipeline) {
		return "%s%s".formatted(pipeline, EXTENSION_YML);
	}

	private File pipelineFile(String pipeline) {
		return new File(directory, fileName(pipeline));
	}

	private record SavedPipeline(PipelineConfigTO pipelineConfig, File file) {
	}
}
