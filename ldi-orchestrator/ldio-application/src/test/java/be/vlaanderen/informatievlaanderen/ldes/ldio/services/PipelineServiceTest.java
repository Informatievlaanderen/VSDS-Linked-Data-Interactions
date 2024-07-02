package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.PipelineServiceImpl;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.PipelineCreatorService;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.PipelineShutdownEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.persistence.PipelineFileRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldio.status.services.PipelineStatusService;
import be.vlaanderen.informatievlaanderen.ldes.ldio.status.services.PipelineStatusServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class PipelineServiceTest {
    private final String pipelineName = "pipeline";
    private final PipelineCreatorService pipelineCreatorService = mock(PipelineCreatorService.class);
    private final PipelineStatusService pipelineStatusService = mock(PipelineStatusServiceImpl.class);
    private final PipelineFileRepository pipelineRepository = mock(PipelineFileRepository.class);
    private PipelineServiceImpl pipelineService;

    @BeforeEach
    void setup() {
	    pipelineService = new PipelineServiceImpl(pipelineCreatorService, pipelineStatusService, pipelineRepository, mock(ApplicationEventPublisher.class));
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

    @Test
    void when_PipelineShutdown_Then_RemovePipeline() {
        when(pipelineRepository.exists(pipelineName)).thenReturn(true);
        PipelineShutdownEvent pipelineShutdownEvent = new PipelineShutdownEvent(pipelineName);

        pipelineService.handlePipelineShutdown(pipelineShutdownEvent);

        verify(pipelineStatusService).stopPipeline(pipelineName);
        verify(pipelineRepository).delete(pipelineName);
        verify(pipelineCreatorService).removePipeline(pipelineName);
    }
}