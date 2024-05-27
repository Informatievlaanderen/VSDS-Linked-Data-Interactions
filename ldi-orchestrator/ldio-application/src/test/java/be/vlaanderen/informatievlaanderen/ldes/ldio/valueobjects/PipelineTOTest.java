package be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.ResumedPipelineStatus;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.StatusChangeSource.AUTO;
import static org.assertj.core.api.Assertions.assertThat;

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

		PipelineTO pipelineTO = PipelineTO.build(createdPipeline, new ResumedPipelineStatus(), AUTO);

		assertThat(pipelineTO.name()).isEqualTo("pipeline");
		assertThat(pipelineTO.status()).isInstanceOf(ResumedPipelineStatus.class);
		assertThat(pipelineTO.updateSource()).isEqualTo(AUTO);
		assertThat(pipelineTO.input())
				.hasFieldOrPropertyWithValue("name", "in")
				.matches(input -> input.getConfig().containsKey("type"))
				.extracting("adapter", InstanceOfAssertFactories.optional(ComponentDefinitionTO.class))
				.contains(new ComponentDefinitionTO("adapter", Map.of("type", "adapter")));
		assertThat(pipelineTO.transformers())
				.allMatch(compDef -> compDef.config().containsKey("type"))
				.map(ComponentDefinitionTO::name)
				.containsExactlyInAnyOrder("t1", "t2");
		assertThat(pipelineTO.outputs())
				.allMatch(compDef -> compDef.config().containsKey("type"))
				.map(ComponentDefinitionTO::name)
				.containsExactly("output");
	}
}