package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.PipelineStatusManager;
import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.PipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.StartedPipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class PipelineStatusServiceTest {
	private final String pipelineName = "pipeline";
	private final LdioInput input = mock(LdioInput.class);
	private final PipelineStatusManager pipelineStatusManager = PipelineStatusManager.initialize(pipelineName, input, List.of());
	private PipelineStatusService pipelineStatusService;

	@BeforeEach
	void setup() {
		pipelineStatusService = new PipelineStatusService();
		pipelineStatusService.handlePipelineCreated(new PipelineCreatedEvent(pipelineStatusManager));
	}

	@Test
	void when_StoppingPipeline_Then_MethodsAreCalled() {
		PipelineStatus.Value result = pipelineStatusService.stopPipeline(pipelineName);

		assertThat(result).isEqualTo(PipelineStatus.Value.STOPPED);
	}

	@Test
	void test_GetPipelineStatusOverview() {
		final Map<String, PipelineStatus.Value> expectedStatusOverview = Map.of("pipeline", PipelineStatus.Value.INIT,
				"second-pipeline", PipelineStatus.Value.RUNNING);
		final var secondPipeline = PipelineStatusManager.initializeWithStatus("second-pipeline", mock(LdioInput.class), List.of(), new StartedPipelineStatus());
		pipelineStatusService.handlePipelineCreated(new PipelineCreatedEvent(secondPipeline));

		final Map<String, PipelineStatus.Value> result = pipelineStatusService.getPipelineStatusOverview();

		assertThat(result).containsExactlyInAnyOrderEntriesOf(expectedStatusOverview);
	}
}
