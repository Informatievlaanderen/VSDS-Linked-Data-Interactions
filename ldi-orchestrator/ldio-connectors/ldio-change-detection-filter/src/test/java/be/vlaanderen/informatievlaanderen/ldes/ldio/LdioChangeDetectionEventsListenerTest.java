package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.ChangeDetectionFilter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioChangeDetectionEventsListenerConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineStatusEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioPipelineEventsListener;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.StatusChangeSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@SpringBootTest(classes = LdioChangeDetectionEventsListenerConfig.class)
@ExtendWith(value = {MockitoExtension.class})
class LdioChangeDetectionEventsListenerTest {
	@SpyBean
	private LdioPipelineEventsListener<LdioChangeDetectionFilter> pipelineEventsListener;
	@Autowired
	private ApplicationEventPublisher eventPublisher;
	private final String pipelineName = "pipeline-name";
	@Mock
	private ChangeDetectionFilter filter;
	private LdioChangeDetectionFilter ldioChangeDetectionFilter;

	@BeforeEach
	void setUp() {
		ldioChangeDetectionFilter = new LdioChangeDetectionFilter(filter);
		pipelineEventsListener.registerComponent(pipelineName, ldioChangeDetectionFilter);
	}

	@Test
	void when_PipelineDeletedEventIsPublished_then_DestroyFilter() {
		eventPublisher.publishEvent(new PipelineDeletedEvent(pipelineName));

		verify(pipelineEventsListener).handlePipelineDeletedEvent(new PipelineDeletedEvent(pipelineName));
		verify(filter).destroyState();
	}

	@Test
	void given_OtherPipeline_when_PublishPipelineHaltedEvent_then_DoNothingWithMaterialiser() {
		final PipelineStatusEvent event = new PipelineStatusEvent("other-pipeline", PipelineStatus.HALTED, StatusChangeSource.MANUAL);

		eventPublisher.publishEvent(event);

		verify(pipelineEventsListener).handlePipelineHaltedEvent(event);
		verifyNoInteractions(filter);
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

		verify(pipelineEventsListener).registerComponent(pipelineName, ldioChangeDetectionFilter);
		verifyNoInteractions(filter);
	}

	static class PipelineStatusArgumentsProvider implements ArgumentsProvider {
		@Override
		public Stream<Arguments> provideArguments(ExtensionContext extensionContext) {
			return Arrays.stream(PipelineStatus.values())
					.filter(status -> !status.equals(PipelineStatus.STOPPED))
					.map(Arguments::of);
		}
	}
}
