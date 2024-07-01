package be.vlaanderen.informatievlaanderen.ldes.ldio.types;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.events.InputCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.events.PipelineDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.events.PipelineStatusEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.services.LdioPipelineEventsListener;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.services.PipelineStatusChangedBehavior;
import be.vlaanderen.informatievlaanderen.ldes.ldio.status.PipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.status.StatusChangeSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LdioPipelineEventsListenerTest {
	private static final String PIPELINE_NAME = "pipeline";
	@Mock
	private PipelineStatusChangedBehavior<LdiComponent> startBehavior;
	@Mock
	private PipelineStatusChangedBehavior<LdiComponent> pauseBehavior;
	@Mock
	private PipelineStatusChangedBehavior<LdiComponent> resumeBehavior;
	@Mock
	private PipelineStatusChangedBehavior<LdiComponent> stopBehavior;

	@Nested
	class ExistingLdioComponentAndBehaviors {
		private LdioPipelineEventsListener<LdiComponent> pipelineEventsListener;

		@BeforeEach
		void setUp() {
			pipelineEventsListener = new LdioPipelineEventsListener.Builder<>()
					.withStartBehavior(startBehavior)
					.withPauseBehavior(pauseBehavior)
					.withResumeBehavior(resumeBehavior)
					.withStopBehavior(stopBehavior)
					.build();
			pipelineEventsListener.registerComponent(PIPELINE_NAME, mock());
		}

		@Test
		void test_start() {
			pipelineEventsListener.handlePipelineRunningEvent(new InputCreatedEvent(PIPELINE_NAME, null));

			verify(startBehavior).applyNewStatus(any());
		}

		@Test
		void test_resume() {
			pipelineEventsListener.handlePipelineRunningEvent(new PipelineStatusEvent(PIPELINE_NAME, PipelineStatus.RUNNING, StatusChangeSource.AUTO));

			verify(resumeBehavior).applyNewStatus(any());
		}

		@Test
		void test_pause() {
			pipelineEventsListener.handlePipelineHaltedEvent(new PipelineStatusEvent(PIPELINE_NAME, PipelineStatus.HALTED, StatusChangeSource.AUTO));

			verify(pauseBehavior).applyNewStatus(any());
		}

		@Test
		void test_stop() {
			pipelineEventsListener.handlePipelineDeletedEvent(new PipelineDeletedEvent(PIPELINE_NAME));

			verify(stopBehavior).applyNewStatus(any());
		}
	}

	@Nested
	class IncompletePipelineEventListener {

		@ParameterizedTest
		@ArgumentsSource(PipelineEventListenerProvider.class)
		void test_start(LdioPipelineEventsListener<LdiComponent> pipelineEventsListener) {
			pipelineEventsListener.handlePipelineRunningEvent(new InputCreatedEvent(PIPELINE_NAME, null));

			verifyNoInteractions(startBehavior);
		}

		@ParameterizedTest
		@ArgumentsSource(PipelineEventListenerProvider.class)
		void test_resume(LdioPipelineEventsListener<LdiComponent> pipelineEventsListener) {
			pipelineEventsListener.handlePipelineRunningEvent(new PipelineStatusEvent(PIPELINE_NAME, PipelineStatus.RUNNING, StatusChangeSource.AUTO));

			verifyNoInteractions(resumeBehavior);
		}

		@ParameterizedTest
		@ArgumentsSource(PipelineEventListenerProvider.class)
		void test_pause(LdioPipelineEventsListener<LdiComponent> pipelineEventsListener) {
			pipelineEventsListener.handlePipelineHaltedEvent(new PipelineStatusEvent(PIPELINE_NAME, PipelineStatus.HALTED, StatusChangeSource.AUTO));

			verifyNoInteractions(pauseBehavior);
		}

		@ParameterizedTest
		@ArgumentsSource(PipelineEventListenerProvider.class)
		void test_stop(LdioPipelineEventsListener<LdiComponent> pipelineEventsListener) {
			pipelineEventsListener.handlePipelineDeletedEvent(new PipelineDeletedEvent(PIPELINE_NAME));

			verifyNoInteractions(stopBehavior);
		}
	}

	static class PipelineEventListenerProvider implements ArgumentsProvider {
		@Override
		public Stream<Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(createListenerWithoutBehaviors(), createListenerWithoutLdioComponent()).map(Arguments::of);
		}

		private LdioPipelineEventsListener<LdiComponent> createListenerWithoutBehaviors() {
			final LdioPipelineEventsListener<LdiComponent> pipelineEventsListener = new LdioPipelineEventsListener.Builder<>().build();
			pipelineEventsListener.registerComponent(PIPELINE_NAME, mock());
			return pipelineEventsListener;
		}

		private LdioPipelineEventsListener<LdiComponent> createListenerWithoutLdioComponent() {
			return new LdioPipelineEventsListener.Builder<>().build();
		}
	}

}