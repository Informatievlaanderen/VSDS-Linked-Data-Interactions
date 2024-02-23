package be.vlaanderen.informatievlaanderen.ldes.ldio.repositories;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.OrchestratorConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.PipelineAlreadyExistsException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.PipelineParsingException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentDefinition;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.InputComponentDefinition;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineConfigTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.repositories.PipelineFileRepository.EXTENSION_YAML;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.repositories.PipelineFileRepository.EXTENSION_YML;
import static org.junit.jupiter.api.Assertions.*;

public class PipelineFileRepositoryTest {
	private final String existingPipeline = "valid-pipeline";
	private final String existingPipelineFile = "valid.yml";
	private Map<File, PipelineConfigTO> initConfig;
	private PipelineFileRepository repository;
	private File testDirectory;

	public static Map<File, PipelineConfigTO> getInitialFiles(File testDirectory) {
		testDirectory.mkdirs();
		try (Stream<Path> files = Files.list(testDirectory.toPath())) {
			return files
					.filter(path -> !Files.isDirectory(path))
					.filter(path -> path.toFile().getName().endsWith(EXTENSION_YML)
					                || path.toFile().getName().endsWith(EXTENSION_YAML))
					.map(path -> {
						try {
							return Map.of(path.toFile(), readConfigFile(path));
						} catch (PipelineParsingException e) {
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

	private static PipelineConfigTO readConfigFile(Path path) throws PipelineParsingException {
		try {
			ObjectReader reader = new ObjectMapper(new YAMLFactory()).readerFor(PipelineConfig.class);
			return reader.readValue(path.toFile(), PipelineConfigTO.class);
		} catch (IOException e) {
			throw new PipelineParsingException(path.getFileName().toString());
		}
	}

	@Test
	void findAll() {
		var activePipelines = repository.getActivePipelines();
		var storedPipelines = repository.getInactivePipelines();

		assertEquals(0, activePipelines.size());
		assertEquals(1, storedPipelines.size());
		assertEquals(existingPipelineFile, storedPipelines.keySet().stream().toList().get(0).getName());
		assertEquals(existingPipeline, storedPipelines.values().stream().toList().get(0).name());
	}

	@BeforeEach
	void setUp() {
		String testDirectoryPath = "src/test/resources/repository";
		testDirectory = new File(testDirectoryPath);
		OrchestratorConfig orchestratorConfig = new OrchestratorConfig();
		orchestratorConfig.setDirectory(testDirectoryPath);
		initConfig = getInitialFiles(testDirectory);
		repository = new PipelineFileRepository(orchestratorConfig);
	}

	@Test
	void saveExistingFile() {
		var storedPipeline = repository.getInactivePipelines()
				.entrySet()
				.stream()
				.findFirst()
				.orElseThrow();

		// Init pipeline
		repository.activateExistingPipeline(storedPipeline.getValue().toPipelineConfig(), storedPipeline.getKey());
		assertThrows(PipelineAlreadyExistsException.class, () -> repository.activateNewPipeline(storedPipeline.getValue().toPipelineConfig()));
	}

	@Test
	void parsingError() {
		Path invalidFile = new File("src/test/resources/repository/invalid.yml").toPath();

		assertThrows(PipelineParsingException.class, () -> repository.readConfigFile(invalidFile));
	}

	@AfterEach
	void cleanup() {
		repository.getActivePipelines()
				.stream()
				.filter(pipelineConfigTO -> !initConfig.containsValue(pipelineConfigTO))
				.forEach(pipelineConfigTO -> repository.delete(pipelineConfigTO.name()));
		var dirFiles = testDirectory.listFiles();
		if (dirFiles != null && dirFiles.length == 0) {
			testDirectory.delete();
		}
	}

	@Test
	void repostitoryFlow() {
		var filesInDirectory = filesInTestDirectory();
		var activePipelines = repository.getActivePipelines();
		var inactivePipelines = repository.getInactivePipelines();

		assertEquals(3, filesInDirectory.size());
		assertEquals(0, activePipelines.size());
		assertEquals(1, inactivePipelines.size());
		assertTrue(inactivePipelines.keySet().stream().anyMatch(file -> existingPipelineFile.equals(file.getName())));
		assertTrue(inactivePipelines.values().stream().anyMatch(pipeline -> pipeline.name().equals(existingPipeline)));

		inactivePipelines.forEach((file, pipelineConfigTO) ->
				repository.activateExistingPipeline(pipelineConfigTO.toPipelineConfig(), file));

		activePipelines = repository.getActivePipelines();
		inactivePipelines = repository.getInactivePipelines();

		assertEquals(1, activePipelines.size());
		assertEquals(0, inactivePipelines.size());
		assertEquals(existingPipeline, activePipelines.get(0).name());

		var config = new PipelineConfig();
		String pipelineName = "valid";
		config.setName(pipelineName);
		config.setInput(new InputComponentDefinition(pipelineName, "Ldio:TestIn", Map.of(), null));
		config.setOutputs(List.of(new ComponentDefinition(pipelineName, "Ldio:TestOut", Map.of())));

		repository.activateNewPipeline(config);

		activePipelines = repository.getActivePipelines();
		inactivePipelines = repository.getInactivePipelines();

		assertEquals(2, activePipelines.size());
		assertEquals(0, inactivePipelines.size());

		filesInDirectory = filesInTestDirectory();

		assertEquals(4, filesInDirectory.size());
		assertTrue(filesInDirectory.stream().anyMatch(file -> (pipelineName + "(1).yml").equals(file.getName())));

		repository.delete(pipelineName);

		activePipelines = repository.getActivePipelines();
		inactivePipelines = repository.getInactivePipelines();

		assertEquals(1, activePipelines.size());
		assertEquals(0, inactivePipelines.size());
		assertFalse(repository.exists(pipelineName));
		assertTrue(repository.exists(existingPipeline));
	}

	private List<File> filesInTestDirectory() {
		return Arrays.stream(Objects.requireNonNull(testDirectory.listFiles()))
				.toList();
	}
}