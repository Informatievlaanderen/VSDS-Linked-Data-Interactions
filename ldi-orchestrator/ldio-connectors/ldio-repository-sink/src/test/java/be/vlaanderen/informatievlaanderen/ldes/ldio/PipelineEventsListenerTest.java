package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.RepositorySink;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioRepositoryPipelineEventsListenerConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.events.PipelineDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.status.LdioPipelineEventsListener;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.status.PipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.status.StatusChangeSource;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.status.events.PipelineStatusEvent;
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
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = LdioRepositoryPipelineEventsListenerConfig.class)
@ExtendWith(value = {MockitoExtension.class})
class PipelineEventsListenerTest {
	@SpyBean
	private LdioPipelineEventsListener<LdioRepositorySink> pipelineEventsListener;
	@Autowired
	private ApplicationEventPublisher eventPublisher;
	private final String pipelineName = "pipeline-name";
	@Mock
	private RepositorySink repositorySink;
	private LdioRepositorySink ldioRepositorySink;

	@BeforeEach
	void setUp() {
		ldioRepositorySink = new LdioRepositorySink(repositorySink, 10, 60000);
		ldioRepositorySink.start();
		pipelineEventsListener.registerComponent(pipelineName, ldioRepositorySink);
	}

	@Test
	void when_PipelineDeletedEventIsPublished_then_ShutdownMaterialiser() {
		eventPublisher.publishEvent(new PipelineDeletedEvent(pipelineName));

		verify(pipelineEventsListener).handlePipelineDeletedEvent(new PipelineDeletedEvent(pipelineName));
		verify(repositorySink).shutdown();
	}

	@Test
	void given_EmptyListOfMembersToCommit_when_PublishPipelineHaltedEvent_then_ShutdownMaterialiser() {
		final PipelineStatusEvent event = new PipelineStatusEvent(pipelineName, PipelineStatus.HALTED, StatusChangeSource.MANUAL);

		eventPublisher.publishEvent(event);

		verify(pipelineEventsListener).handlePipelineHaltedEvent(event);
		verify(repositorySink).shutdown();
		verifyNoMoreInteractions(repositorySink);
	}

	@Test
	void given_NonEmptyListOfMembersToCommit_when_PublishPipelineHaltedEvent_then_CommitMembers_and_ShutdownMaterialiser() {
		ldioRepositorySink.accept(ModelFactory.createDefaultModel());
		final PipelineStatusEvent event = new PipelineStatusEvent(pipelineName, PipelineStatus.HALTED, StatusChangeSource.MANUAL);

		eventPublisher.publishEvent(event);

		verify(pipelineEventsListener).handlePipelineHaltedEvent(event);
		verify(repositorySink).process(anyList());
		verify(repositorySink).shutdown();
		verifyNoMoreInteractions(repositorySink);
	}

	@Test
	void given_OtherPipeline_when_PublishPipelineHaltedEvent_then_DoNothingWithMaterialiser() {
		final PipelineStatusEvent event = new PipelineStatusEvent("other-pipeline", PipelineStatus.HALTED, StatusChangeSource.MANUAL);

		eventPublisher.publishEvent(event);

		verify(pipelineEventsListener).handlePipelineHaltedEvent(event);
		verifyNoInteractions(repositorySink);
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

		verify(pipelineEventsListener).registerComponent(pipelineName, ldioRepositorySink);
		verifyNoMoreInteractions(pipelineEventsListener);
		verifyNoInteractions(repositorySink);
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
