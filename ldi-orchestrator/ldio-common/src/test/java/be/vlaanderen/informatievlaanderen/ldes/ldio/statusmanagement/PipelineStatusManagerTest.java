package be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement;

import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.*;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioStatusOutput;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class PipelineStatusManagerTest {
	public static final String PIPELINE_NAME = "my-pipeline";
	private PipelineStatusManager pipelineStatusManager;

	@ParameterizedTest
	@ArgumentsSource(IncompatibleInitStatusesProvider.class)
	void given_newPipeline_when_initializeWithInvalidInitStatus_then_DoNothing(PipelineStatus initializingStatus) {
		pipelineStatusManager = PipelineStatusManager.initializeWithStatus(PIPELINE_NAME, mock(), List.of(mock(LdioStatusOutput.class)), initializingStatus);

		assertThat(pipelineStatusManager.getPipelineStatus()).isNull();
	}

	@ParameterizedTest
	@ArgumentsSource(IncompatibleStatusesProviderForInitStatus.class)
	void given_InitPipelineStatus_test_IncompatibleStatusUpdate(PipelineStatus currentStatus, PipelineStatus newStatus) {
		initializePipelineWith(new InitPipelineStatus());
		pipelineStatusManager.updatePipelineStatus(currentStatus);

		pipelineStatusManager.updatePipelineStatus(newStatus);
		final var updatedStatus = pipelineStatusManager.getPipelineStatus();

		assertThat(updatedStatus)
				.isInstanceOf(currentStatus.getClass())
				.isNotInstanceOf(newStatus.getClass());
	}

	@ParameterizedTest
	@ArgumentsSource(IncompatibleStatusesProviderForStartedStatus.class)
	void given_StartedPipelineStatus_test_IncompatibleStatusUpdate(PipelineStatus currentStatus, PipelineStatus newStatus) {
		initializePipelineWith(new StartedPipelineStatus());
		pipelineStatusManager.updatePipelineStatus(currentStatus);

		pipelineStatusManager.updatePipelineStatus(newStatus);
		final var updatedStatus = pipelineStatusManager.getPipelineStatus();

		assertThat(updatedStatus)
				.isInstanceOf(currentStatus.getClass())
				.isNotInstanceOf(newStatus.getClass());
	}

	private void initializePipelineWith(PipelineStatus pipelineStatus) {
		pipelineStatusManager = PipelineStatusManager.initializeWithStatus(PIPELINE_NAME, mock(), List.of(mock(LdioStatusOutput.class)), pipelineStatus);
	}

	@TestFactory
	Stream<DynamicTest> test_correctStatusFlows() {
		return Stream.of(
						List.of(new InitPipelineStatus(), new StoppedPipelineStatus()),
						List.of(new InitPipelineStatus(), new ResumedPipelineStatus(), new StoppedPipelineStatus()),
						List.of(new StartedPipelineStatus(), new StoppedPipelineStatus()),
						List.of(new StartedPipelineStatus(), new HaltedPipelineStatus(), new StoppedPipelineStatus()),
						List.of(new StartedPipelineStatus(), new HaltedPipelineStatus(), new ResumedPipelineStatus(), new StoppedPipelineStatus()),
						List.of(new StartedPipelineStatus(), new HaltedPipelineStatus(), new ResumedPipelineStatus(), new HaltedPipelineStatus(), new StoppedPipelineStatus())
				)
				.map(LinkedList::new)
				.map(flow -> DynamicTest.dynamicTest(joinForName(flow), () -> {
					initializePipelineWith(flow.poll());
					int expectedCount = flow.size();

					int updateCounter = 0;
					while (!flow.isEmpty()) {
						final var nextStatus = flow.poll();
						pipelineStatusManager.updatePipelineStatus(nextStatus);
						updateCounter++;
					}

					assertThat(updateCounter).isEqualTo(expectedCount);
					assertThat(pipelineStatusManager.getPipelineStatus()).isInstanceOf(StoppedPipelineStatus.class);
				}));

	}

	static class IncompatibleInitStatusesProvider implements ArgumentsProvider {
		@Override
		public Stream<Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream
					.of(new ResumedPipelineStatus(), new HaltedPipelineStatus(), new StoppedPipelineStatus())
					.map(Arguments::of);
		}
	}

	static class IncompatibleStatusesProviderForInitStatus implements ArgumentsProvider {
		@Override
		public Stream<Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(
					Arguments.of(new InitPipelineStatus(), new StartedPipelineStatus()),
					Arguments.of(new InitPipelineStatus(), new HaltedPipelineStatus()),
					Arguments.of(new ResumedPipelineStatus(), new StartedPipelineStatus()),
					Arguments.of(new ResumedPipelineStatus(), new InitPipelineStatus()),
					Arguments.of(new StoppedPipelineStatus(), new InitPipelineStatus()),
					Arguments.of(new StoppedPipelineStatus(), new StartedPipelineStatus()),
					Arguments.of(new StoppedPipelineStatus(), new ResumedPipelineStatus()),
					Arguments.of(new StoppedPipelineStatus(), new HaltedPipelineStatus())
			);
		}
	}

	static class IncompatibleStatusesProviderForStartedStatus implements ArgumentsProvider {
		@Override
		public Stream<Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(
					Arguments.of(new StoppedPipelineStatus(), new InitPipelineStatus()),
					Arguments.of(new StoppedPipelineStatus(), new StartedPipelineStatus()),
					Arguments.of(new StoppedPipelineStatus(), new ResumedPipelineStatus()),
					Arguments.of(new StoppedPipelineStatus(), new HaltedPipelineStatus()),
					Arguments.of(new StartedPipelineStatus(), new InitPipelineStatus()),
					Arguments.of(new HaltedPipelineStatus(), new StartedPipelineStatus()),
					Arguments.of(new HaltedPipelineStatus(), new InitPipelineStatus())
			);
		}
	}

	private String joinForName(List<PipelineStatus> statusesFlow) {
		return statusesFlow.stream()
				.map(status -> status.getClass().getSimpleName())
				.collect(Collectors.joining(" -> "));
	}

}