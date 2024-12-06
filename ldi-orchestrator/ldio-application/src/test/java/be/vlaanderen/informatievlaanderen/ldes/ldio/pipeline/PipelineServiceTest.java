package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline;

import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.PipelineCreatorService;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.events.PipelineShutdownEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.exception.PipelineAlreadyExistsException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.exception.PipelineInitialisationException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.persistence.PipelineFileRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.status.PipelineStatusService;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.status.PipelineStatusServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PipelineServiceTest {
	private final String pipelineName = "pipeline";
	private final PipelineCreatorService pipelineCreatorService = mock(PipelineCreatorService.class);
	private final PipelineStatusService pipelineStatusService = mock(PipelineStatusServiceImpl.class);
	private final PipelineFileRepository pipelineRepository = mock(PipelineFileRepository.class);
	private PipelineServiceImpl pipelineService;

	@BeforeEach
	void setup() {
		pipelineService = new PipelineServiceImpl(pipelineCreatorService, pipelineStatusService, pipelineRepository);
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

	@Nested
	class PipelineCreation {
		@Test
		void when_AddPipeline_Then_ExpectPipelineCreated() {
			PipelineConfig pipelineConfig = new PipelineConfig();
			pipelineConfig.setName(pipelineName);
			when(pipelineRepository.exists(pipelineName)).thenReturn(false);

			pipelineService.addPipeline(pipelineConfig);

			verify(pipelineCreatorService).initialisePipeline(pipelineConfig);
			verify(pipelineRepository).activateNewPipeline(pipelineConfig);
		}

		@Test
		void when_addPipeline_And_RuntimeException_Then_ExpectPipelineInitialisationException() {
			PipelineConfig pipelineConfig = new PipelineConfig();
			pipelineConfig.setName(pipelineName);
			when(pipelineRepository.exists(pipelineName)).thenReturn(false);
			doThrow(RuntimeException.class).when(pipelineCreatorService).initialisePipeline(pipelineConfig);

			assertThrows(PipelineInitialisationException.class, () -> pipelineService.addPipeline(pipelineConfig));
		}

		@Test
		void when_AddExistingPipeline_Then_ExpectPipelineAlreadyExistsException() {
			PipelineConfig pipelineConfig = new PipelineConfig();
			pipelineConfig.setName(pipelineName);
			when(pipelineRepository.exists(pipelineName)).thenReturn(true);

			assertThrows(PipelineAlreadyExistsException.class, () -> pipelineService.addPipeline(pipelineConfig));
		}
	}

	@Nested
	class PersistedPipelineCreation {
		@Test
		void when_AddPersistedPipeline_Then_ExpectPipelineCreated() {
			PipelineConfig pipelineConfig = new PipelineConfig();
			pipelineConfig.setName(pipelineName);
			File persistedFile = mock(File.class);
			when(pipelineRepository.exists(pipelineName)).thenReturn(false);

			pipelineService.addPipeline(pipelineConfig, persistedFile);

			verify(pipelineCreatorService).initialisePipeline(pipelineConfig);
			verify(pipelineRepository).activateExistingPipeline(pipelineConfig, persistedFile);
		}

		@Test
		void when_addPersistedPipeline_And_RuntimeException_Then_ExpectPipelineInitialisationException() {
			PipelineConfig pipelineConfig = new PipelineConfig();
			pipelineConfig.setName(pipelineName);
			File persistedFile = mock(File.class);
			when(pipelineRepository.exists(pipelineName)).thenReturn(false);
			doThrow(RuntimeException.class).when(pipelineCreatorService).initialisePipeline(pipelineConfig);

			assertThrows(PipelineInitialisationException.class, () -> pipelineService.addPipeline(pipelineConfig, persistedFile));
		}

		@Test
		void when_AddExistingPersistedPipeline_Then_ExpectPipelineAlreadyExistsException() {
			PipelineConfig pipelineConfig = new PipelineConfig();
			pipelineConfig.setName(pipelineName);
			File persistedFile = mock(File.class);
			when(pipelineRepository.exists(pipelineName)).thenReturn(true);

			assertThrows(PipelineAlreadyExistsException.class, () -> pipelineService.addPipeline(pipelineConfig, persistedFile));
		}
	}

}