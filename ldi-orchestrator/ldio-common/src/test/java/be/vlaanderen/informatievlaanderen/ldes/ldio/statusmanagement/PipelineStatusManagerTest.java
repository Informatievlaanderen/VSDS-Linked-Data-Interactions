package be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement;

import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.*;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioStatusOutput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class PipelineStatusManagerTest {
	public static final String PIPELINE_NAME = "my-pipeline";
	@Mock
	private LdioInput ldioInput;
	@Mock
	private LdioStatusOutput ldioOutput;
	private PipelineStatusManager pipelineStatusManager;


	@Test
	void test_InitPipeline() {
		pipelineStatusManager = PipelineStatusManager.initialize(PIPELINE_NAME, ldioInput, List.of(ldioOutput));
		final var currentStatus = pipelineStatusManager.getPipelineStatusValue();

		assertThat(currentStatus).isEqualTo(PipelineStatus.Value.INIT);
		verify(ldioInput).start();
		verify(ldioOutput).start();
	}

	@Test
	void test_StartPipeline() {
		initializePipelineWith(new StartedPipelineStatus());
		final var currentStatus = pipelineStatusManager.getPipelineStatusValue();

		assertThat(currentStatus).isEqualTo(PipelineStatus.Value.RUNNING);
		verify(ldioInput).start();
		verify(ldioOutput).start();
	}

	@Test
	void test_ResumeHaltedPipeline() {
		initializePipelineWith(new StartedPipelineStatus());
		pipelineStatusManager.updatePipelineStatus(new HaltedPipelineStatus());

		pipelineStatusManager.updatePipelineStatus(new RunningPipelineStatus());
		final var currentStatus = pipelineStatusManager.getPipelineStatusValue();

		assertThat(currentStatus).isEqualTo(PipelineStatus.Value.RUNNING);
		verify(ldioInput).resume();
		verify(ldioOutput).resume();
	}

	@Test
	void given_StartedPipeline_test_HaltPipeline() {
		initializePipelineWith(new StartedPipelineStatus());

		pipelineStatusManager.updatePipelineStatus(new HaltedPipelineStatus());
		final var currentStatus = pipelineStatusManager.getPipelineStatusValue();

		assertThat(currentStatus).isEqualTo(PipelineStatus.Value.HALTED);
		verify(ldioInput).pause();
		verify(ldioOutput).pause();
	}

	@Test
	void given_InitPipeline_test_HaltPipeline() {
		initializePipelineWith(new InitPipelineStatus());
		pipelineStatusManager.updatePipelineStatus(new RunningPipelineStatus());

		pipelineStatusManager.updatePipelineStatus(new HaltedPipelineStatus());
		final var currentStatus = pipelineStatusManager.getPipelineStatusValue();

		assertThat(currentStatus).isEqualTo(PipelineStatus.Value.HALTED);
		verify(ldioInput).pause();
		verify(ldioOutput).pause();
	}

	@ParameterizedTest
	@ArgumentsSource(IncompatibleInitStatusesProvider.class)
	void given_newPipeline_when_initializeWithInvalidInitStatus_then_DoNothing(PipelineStatus initializingStatus) {
		pipelineStatusManager = PipelineStatusManager.initializeWithStatus(PIPELINE_NAME, ldioInput, List.of(ldioOutput), initializingStatus);

		assertThat(pipelineStatusManager.getPipelineStatus()).isNull();
		verifyNoInteractions(ldioInput, ldioOutput);
	}

	@ParameterizedTest
	@ArgumentsSource(InitialPipelineStatusProvider.class)
	void test_StopAnyPipeline(PipelineStatus initialStatus) {
		initializePipelineWith(new InitPipelineStatus());
		pipelineStatusManager.updatePipelineStatus(initialStatus);

		pipelineStatusManager.updatePipelineStatus(new StoppedPipelineStatus());
		final var currentStatus = pipelineStatusManager.getPipelineStatusValue();

		assertThat(currentStatus).isEqualTo(PipelineStatus.Value.STOPPED);
		verify(ldioInput).shutdown();
		verify(ldioOutput).shutdown();
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
		pipelineStatusManager = PipelineStatusManager.initializeWithStatus(PIPELINE_NAME, ldioInput, List.of(ldioOutput), pipelineStatus);
	}

	static class InitialPipelineStatusProvider implements ArgumentsProvider {
		@Override
		public Stream<Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(
							new InitPipelineStatus(),
							new StartedPipelineStatus(),
							new RunningPipelineStatus(),
							new HaltedPipelineStatus())
					.map(Arguments::of);
		}
	}

	static class IncompatibleInitStatusesProvider implements ArgumentsProvider {
		@Override
		public Stream<Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream
					.of(new RunningPipelineStatus(), new HaltedPipelineStatus(), new StoppedPipelineStatus())
					.map(Arguments::of);
		}
	}

	static class IncompatibleStatusesProviderForInitStatus implements ArgumentsProvider {
		@Override
		public Stream<Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(
					Arguments.of(new InitPipelineStatus(), new StartedPipelineStatus()),
					Arguments.of(new RunningPipelineStatus(), new StartedPipelineStatus()),
					Arguments.of(new RunningPipelineStatus(), new InitPipelineStatus()),
					Arguments.of(new StoppedPipelineStatus(), new InitPipelineStatus()),
					Arguments.of(new StoppedPipelineStatus(), new StartedPipelineStatus()),
					Arguments.of(new StoppedPipelineStatus(), new RunningPipelineStatus()),
					Arguments.of(new StoppedPipelineStatus(), new HaltedPipelineStatus()),
					Arguments.of(new InitPipelineStatus(), new HaltedPipelineStatus())
			);
		}
	}

	static class IncompatibleStatusesProviderForStartedStatus implements ArgumentsProvider {
		@Override
		public Stream<Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(
					Arguments.of(new StoppedPipelineStatus(), new InitPipelineStatus()),
					Arguments.of(new StoppedPipelineStatus(), new StartedPipelineStatus()),
					Arguments.of(new StoppedPipelineStatus(), new RunningPipelineStatus()),
					Arguments.of(new StoppedPipelineStatus(), new HaltedPipelineStatus()),
					Arguments.of(new StartedPipelineStatus(), new InitPipelineStatus()),
					Arguments.of(new HaltedPipelineStatus(), new StartedPipelineStatus()),
					Arguments.of(new HaltedPipelineStatus(), new InitPipelineStatus())
			);
		}
	}

}