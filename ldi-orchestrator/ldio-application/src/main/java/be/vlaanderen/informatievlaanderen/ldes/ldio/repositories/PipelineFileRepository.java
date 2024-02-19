package be.vlaanderen.informatievlaanderen.ldes.ldio.repositories;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PipelineFileRepository implements PipelineRepository {
	public static final String EXTENSION = ".yaml";
	private final File directory = new File("pipelines");
	private final File backupDirectory = new File(directory, "backup");
	Logger log = LoggerFactory.getLogger(PipelineFileRepository.class);
	ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
	ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
	ObjectReader reader = mapper.readerFor(PipelineConfig.class);

	public PipelineFileRepository() {
		directory.mkdirs();
	}

	@Override
	public List<PipelineConfigTO> findAll() {
		return pipelineToFileMapping().values().stream().toList();
	}

	public Map<File, PipelineConfigTO> pipelineToFileMapping() {
		try (Stream<Path> files = Files.list(directory.toPath())) {
			return files
					.filter(path -> !Files.isDirectory(path))
					.filter(path -> path.toFile().getName().endsWith(EXTENSION))
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
	public String save(PipelineConfig pipeline) throws IOException {
		writer.writeValue(pipelineFile(pipeline.getName()), PipelineConfigTO.fromPipelineConfig(pipeline));

		return pipeline.getName();
	}

	@Override
	public boolean delete(String pipelineName) {
		File file = new File(directory, fileName(pipelineName));

		if (!file.exists()) {
			log.error("Failed to delete pipeline {}, pipeline does not exist.", pipelineName);
			return false;
		}
		return file.delete();
	}

	public boolean exists(String pipeline) {
		return pipelineFile(pipeline).exists();
	}

	public void backup(File file) {
		backupDirectory.mkdirs();

		if (!new File(directory, file.getName()).renameTo(new File(backupDirectory, file.getName()))) {
			AtomicInteger count = new AtomicInteger();
			boolean success = false;
			do {
				try {
					String backupName = file.getName().replace(EXTENSION, ("(%d)%s").formatted(count.incrementAndGet(), EXTENSION));
					Files.move(new File(directory, file.getName()).toPath(), new File(backupDirectory, backupName).toPath());
					success = true;
				} catch (IOException ignored) {
				}
			} while (!success);
		}
	}

	public void cleanupBackup(File file) {
		var backupFile = new File(backupDirectory, file.getName());
		if (backupFile.exists()) {
			backupFile.delete();
		}
	}

	public String fileName(String pipeline) {
		return "%s%s".formatted(pipeline, EXTENSION);
	}

	private File pipelineFile(String pipeline) {
		return new File(directory, fileName(pipeline));
	}
}
