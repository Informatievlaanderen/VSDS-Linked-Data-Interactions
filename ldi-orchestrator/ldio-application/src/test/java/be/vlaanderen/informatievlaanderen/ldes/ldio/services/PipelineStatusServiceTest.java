package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.PipelineStatusManager;
import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.PipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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

        assertEquals(PipelineStatus.Value.STOPPED, result);
    }

}
