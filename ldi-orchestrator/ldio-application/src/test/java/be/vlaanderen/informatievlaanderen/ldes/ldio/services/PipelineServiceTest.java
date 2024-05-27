package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import be.vlaanderen.informatievlaanderen.ldes.ldio.repositories.PipelineFileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class PipelineServiceTest {
	private final String pipelineName = "pipeline";
	private final PipelineCreatorService pipelineCreatorService = mock(PipelineCreatorService.class);
	private final PipelineStatusService pipelineStatusService = mock(PipelineStatusService.class);
	private final PipelineFileRepository pipelineRepository = mock(PipelineFileRepository.class);
	private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
	private PipelineService pipelineService;

	@BeforeEach
	void setup() {
		pipelineService = new PipelineService(pipelineCreatorService, pipelineStatusService, pipelineRepository, eventPublisher);
	}

	@Test
	void when_StoppingPipeline_Then_MethodsAreCalled() {
		when(pipelineRepository.exists(pipelineName)).thenReturn(true);

		boolean result = pipelineService.requestDeletion(pipelineName);

		assertTrue(result);
		verify(pipelineStatusService).stopPipeline(pipelineName);
	}

	@Test
	void when_StoppingNonExistingPipeline_Then_NoMethodsAreCalled() {
		when(pipelineRepository.exists(pipelineName)).thenReturn(false);

		boolean result = pipelineService.requestDeletion(pipelineName);

		assertFalse(result);
		verifyNoInteractions(pipelineStatusService);
	}

}