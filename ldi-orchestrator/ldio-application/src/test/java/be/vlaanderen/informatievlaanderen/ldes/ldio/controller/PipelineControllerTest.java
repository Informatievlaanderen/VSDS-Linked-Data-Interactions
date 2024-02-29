package be.vlaanderen.informatievlaanderen.ldes.ldio.controller;

import be.vlaanderen.informatievlaanderen.ldes.ldio.services.PipelineService;
import be.vlaanderen.informatievlaanderen.ldes.ldio.services.PipelineStatusService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class PipelineControllerTest {

    private final PipelineService pipelineService = mock(PipelineService.class);
    private final PipelineStatusService pipelineStatusService = mock(PipelineStatusService.class);
    private final PipelineController controller = new PipelineController(pipelineService, pipelineStatusService);
    @ParameterizedTest
    @ValueSource(strings = { "", "nonsense&é", "true", "True" })
    void testKeepStateTrue(String input) {
        assertTrue(controller.extractKeepState(input));
    }
    @ParameterizedTest
    @ValueSource(strings = { "false", "False" })
    void testKeepStateFalse(String input) {
        assertFalse(controller.extractKeepState(input));
    }

}