package be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus.RUNNING;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.StatusChangeSource.AUTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PipelineTOTest {

	private static PipelineConfigTO getPipelineConfig() {
		String name = "pipeline";
		var input = new InputComponentDefinitionTO("in",
				new ComponentDefinitionTO("adapter", Map.of("type", "adapter")), Map.of("type", "in"));
		var transformers = List.of(
				new ComponentDefinitionTO("t1", Map.of("type", "transform")),
				new ComponentDefinitionTO("t2", Map.of("type", "transform")));
		var outputs = List.of(new ComponentDefinitionTO("output", Map.of("type", "output")));
		return new PipelineConfigTO(name, "", input, transformers, outputs);
	}

	@Test
	void fromPipelineConfig() {
		PipelineConfigTO createdPipeline = getPipelineConfig();

		PipelineTO pipelineTO = PipelineTO.build(createdPipeline, RUNNING, AUTO);

		assertEquals("pipeline", pipelineTO.name());
		assertEquals(RUNNING, pipelineTO.status());
		assertEquals(AUTO, pipelineTO.statusChangeSource());
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