package be.vlaanderen.informatievlaanderen.ldes.ldio.repositories;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.OrchestratorConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.PipelineAlreadyExistsException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.PipelineParsingException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentDefinition;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.InputComponentDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PipelineFileRepositoryTest {
	private final String existingPipeline = "valid-pipeline";
	private final String existingPipelineFile = "valid.yml";
	private PipelineFileRepository repository;

	@BeforeEach
	void setUp() {
		OrchestratorConfig orchestratorConfig = new OrchestratorConfig();
		orchestratorConfig.setDirectory("src/test/resources/repository");
		repository = new PipelineFileRepository(orchestratorConfig);
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

	@Test
	void repostitoryFlow() {
		var storedPipelines = repository.getInactivePipelines();

		assertEquals(1, storedPipelines.size());

		storedPipelines.forEach((file, pipelineConfigTO) ->
				repository.activateExistingPipeline(pipelineConfigTO.toPipelineConfig(), file));

		var activePipelines = repository.getActivePipelines();
		storedPipelines = repository.getInactivePipelines();

		assertEquals(1, activePipelines.size());
		assertEquals(1, storedPipelines.size());
		assertTrue(storedPipelines.keySet().stream().anyMatch(file -> existingPipelineFile.equals(file.getName())));
		assertTrue(storedPipelines.values().stream().anyMatch(pipeline -> pipeline.name().equals(existingPipeline)));

		var config = new PipelineConfig();
		String pipelineName = "valid";
		config.setName(pipelineName);
		config.setInput(new InputComponentDefinition(pipelineName, "Ldio:TestIn", Map.of(), null));
		config.setOutputs(List.of(new ComponentDefinition(pipelineName, "Ldio:TestOut", Map.of())));

		repository.activateNewPipeline(config);

		activePipelines = repository.getActivePipelines();
		storedPipelines = repository.getInactivePipelines();

		assertEquals(2, activePipelines.size());
		assertEquals(2, storedPipelines.size());
		assertTrue(storedPipelines.keySet().stream().anyMatch(file -> (pipelineName + "(1).yml").equals(file.getName())));
		assertTrue(storedPipelines.values().stream().anyMatch(pipeline -> pipeline.name().equals(pipelineName)));

		repository.delete(pipelineName);

		activePipelines = repository.getActivePipelines();
		storedPipelines = repository.getInactivePipelines();

		assertEquals(1, activePipelines.size());
		assertEquals(1, storedPipelines.size());
		assertFalse(repository.exists(pipelineName));
		assertTrue(repository.exists(existingPipeline));
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
}