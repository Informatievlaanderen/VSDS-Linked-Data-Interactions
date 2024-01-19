package be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus.RUNNING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PipelineTOTest {

	private static PipelineConfig getPipelineConfig() {
		PipelineConfig createdPipeline = new PipelineConfig();

		createdPipeline.setName("pipeline");
		createdPipeline.setInput(new InputComponentDefinition("in", Map.of("type", "in"),
				new ComponentDefinition("adapter", Map.of("type", "adapter"))));
		createdPipeline.setTransformers(List.of(
				new ComponentDefinition("t1", Map.of("type", "transform")),
				new ComponentDefinition("t2", Map.of("type", "transform")))
		);
		createdPipeline.setOutputs(List.of(new ComponentDefinition("output", Map.of("type", "output"))));
		return createdPipeline;
	}

	@Test
	void fromPipelineConfig() {
		PipelineConfig createdPipeline = getPipelineConfig();

		PipelineTO pipelineTO = PipelineTO.fromPipelineConfig(createdPipeline, RUNNING);

		assertEquals("pipeline", pipelineTO.name());
		assertEquals(RUNNING, pipelineTO.status());
		assertEquals("in", pipelineTO.input().name());
		assertTrue(pipelineTO.input().config().containsKey("type"));
		assertEquals("adapter", pipelineTO.input().adapter().name());
		assertTrue(pipelineTO.input().adapter().config().containsKey("type"));
		assertEquals(2, pipelineTO.transformers().size());
		assertEquals("t1", pipelineTO.transformers().get(0).name());
		assertEquals("t2", pipelineTO.transformers().get(1).name());
		assertTrue(pipelineTO.transformers().stream().allMatch(cd -> cd.config().containsKey("type")));
		assertEquals(1, pipelineTO.outputs().size());
		assertEquals("output", pipelineTO.outputs().get(0).name());
		assertTrue(pipelineTO.outputs().stream().allMatch(cd -> cd.config().containsKey("type")));
	}
}