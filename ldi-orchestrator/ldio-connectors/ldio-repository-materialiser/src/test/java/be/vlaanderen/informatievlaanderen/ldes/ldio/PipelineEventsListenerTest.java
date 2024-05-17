package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.Materialiser;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioRepositoryPipelineEventsListenerConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineStatusEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioPipelineEventsListener;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.StatusChangeSource;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = LdioRepositoryPipelineEventsListenerConfig.class)
@ExtendWith(value = {MockitoExtension.class})
class PipelineEventsListenerTest {
	@SpyBean
	private LdioPipelineEventsListener<LdioRepositoryMaterialiser> pipelineEventsListener;
	@Autowired
	private ApplicationEventPublisher eventPublisher;
	private final String pipelineName = "pipeline-name";
	@Mock
	private Materialiser materialiser;
	private LdioRepositoryMaterialiser ldioRepositoryMaterialiser;

	@BeforeEach
	void setUp() {
		ldioRepositoryMaterialiser = new LdioRepositoryMaterialiser(materialiser, 10, 60000);
		ldioRepositoryMaterialiser.start();
		pipelineEventsListener.registerComponent(pipelineName, ldioRepositoryMaterialiser);
	}

	@Test
	void when_PipelineDeletedEventIsPublished_then_ShutdownMaterialiser() {
		eventPublisher.publishEvent(new PipelineDeletedEvent(pipelineName));

		verify(pipelineEventsListener).handlePipelineDeletedEvent(new PipelineDeletedEvent(pipelineName));
		verify(materialiser).shutdown();
	}

	@Test
	void given_EmptyListOfMembersToCommit_when_PublishPipelineHaltedEvent_then_ShutdownMaterialiser() {
		final PipelineStatusEvent event = new PipelineStatusEvent(pipelineName, PipelineStatus.HALTED, StatusChangeSource.MANUAL);

		eventPublisher.publishEvent(event);

		verify(pipelineEventsListener).handlePipelineHaltedEvent(event);
		verify(materialiser).shutdown();
		verifyNoMoreInteractions(materialiser);
	}

	@Test
	void given_NonEmptyListOfMembersToCommit_when_PublishPipelineHaltedEvent_then_CommitMembers_and_ShutdownMaterialiser() {
		when(materialiser.processAsync(anyList())).thenReturn(new CompletableFuture<>());
		ldioRepositoryMaterialiser.accept(ModelFactory.createDefaultModel());
		final PipelineStatusEvent event = new PipelineStatusEvent(pipelineName, PipelineStatus.HALTED, StatusChangeSource.MANUAL);

		eventPublisher.publishEvent(event);

		verify(pipelineEventsListener).handlePipelineHaltedEvent(event);
		verify(materialiser).processAsync(anyList());
		verify(materialiser).shutdown();
		verifyNoMoreInteractions(materialiser);
	}

	@Test
	void given_OtherPipeline_when_PublishPipelineHaltedEvent_then_DoNothingWithMaterialiser() {
		final PipelineStatusEvent event = new PipelineStatusEvent("other-pipeline", PipelineStatus.HALTED, StatusChangeSource.MANUAL);

		eventPublisher.publishEvent(event);

		verify(pipelineEventsListener).handlePipelineHaltedEvent(event);
		verifyNoInteractions(materialiser);
	}

	@Test
	void when_PublishPipelineRunningEvent_then_handleEvent() {
		final PipelineStatusEvent event = new PipelineStatusEvent(pipelineName, PipelineStatus.RUNNING, StatusChangeSource.MANUAL);

		eventPublisher.publishEvent(event);

		verify(pipelineEventsListener).handlePipelineRunningEvent(event);
	}

	@ParameterizedTest
	@ArgumentsSource(PipelineStatusArgumentsProvider.class)
	void when_PublishNonRunningOrHaltedEvents_then_HandleNothing(PipelineStatus pipelineStatus) {
		final PipelineStatusEvent event = new PipelineStatusEvent(pipelineName, pipelineStatus, StatusChangeSource.MANUAL);

		eventPublisher.publishEvent(event);

		verify(pipelineEventsListener).registerComponent(pipelineName, ldioRepositoryMaterialiser);
		verifyNoMoreInteractions(pipelineEventsListener);
		verifyNoInteractions(materialiser);
	}

	static class PipelineStatusArgumentsProvider implements ArgumentsProvider {
		@Override
		public Stream<Arguments> provideArguments(ExtensionContext extensionContext) {
			return Arrays.stream(PipelineStatus.values())
					.filter(status -> !(status.equals(PipelineStatus.HALTED) || status.equals(PipelineStatus.RUNNING)))
					.map(Arguments::of);
		}
	}
}
