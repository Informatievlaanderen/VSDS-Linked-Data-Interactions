package be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement;

import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.*;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioStatusOutput;
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

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PipelineStatusManagerTest {
	public static final String PIPELINE_NAME = "my-pipeline";
	@Mock
	private LdioInput ldioInput;
	@Mock
	private LdioStatusOutput ldioOutput;
	private PipelineStatusManager pipelineStatusManager;

	@BeforeEach
	void setUp() {
		pipelineStatusManager = PipelineStatusManager.initialize(PIPELINE_NAME, ldioInput, List.of(ldioOutput));
	}

	@Test
	void test_InitPipeline() {
		final var currentStatus = pipelineStatusManager.getPipelineStatusValue();

		assertThat(currentStatus).isEqualTo(PipelineStatus.Value.INIT);
		verify(ldioInput).start();
		verify(ldioOutput).start();
	}

	@Test
	void test_StartPipeline() {
		final var ldioInput = mock(LdioInput.class);
		final var ldioOutput = mock(LdioStatusOutput.class);
		final var startedPipelineStatusManager = PipelineStatusManager.initializeWithStatus(PIPELINE_NAME, ldioInput, List.of(ldioOutput), new StartedPipelineStatus());

		final var currentStatus = startedPipelineStatusManager.getPipelineStatusValue();

		assertThat(currentStatus).isEqualTo(PipelineStatus.Value.RUNNING);
		verify(ldioInput).start();
		verify(ldioOutput).start();
	}

	@Test
	void test_ResumeHaltedPipeline() {
		pipelineStatusManager.updatePipelineStatus(new StartedPipelineStatus());
		pipelineStatusManager.updatePipelineStatus(new HaltedPipelineStatus());

		pipelineStatusManager.updatePipelineStatus(new RunningPipelineStatus());
		final var currentStatus = pipelineStatusManager.getPipelineStatusValue();

		assertThat(currentStatus).isEqualTo(PipelineStatus.Value.RUNNING);
		verify(ldioInput).resume();
		verify(ldioOutput).resume();
	}

	@Test
	void test_HaltRunningPipeline() {
		pipelineStatusManager.updatePipelineStatus(new RunningPipelineStatus());

		pipelineStatusManager.updatePipelineStatus(new HaltedPipelineStatus());
		final var currentStatus = pipelineStatusManager.getPipelineStatusValue();

		assertThat(currentStatus).isEqualTo(PipelineStatus.Value.HALTED);
		verify(ldioInput).pause();
		verify(ldioOutput).pause();
	}

	@ParameterizedTest
	@ArgumentsSource(InitialPipelineStatusProvider.class)
	void test_StopAnyPipeline(PipelineStatus initialStatus) {
		pipelineStatusManager.updatePipelineStatus(initialStatus);

		pipelineStatusManager.updatePipelineStatus(new StoppedPipelineStatus());
		final var currentStatus = pipelineStatusManager.getPipelineStatusValue();

		assertThat(currentStatus).isEqualTo(PipelineStatus.Value.STOPPED);
		verify(ldioInput).shutdown();
		verify(ldioOutput).shutdown();
	}

	@ParameterizedTest
	@ArgumentsSource(IncompatibleStatusesProvider.class)
	void test_IncompatibleStatusUpdate(PipelineStatus currentStatus, PipelineStatus newStatus) {
		pipelineStatusManager.updatePipelineStatus(currentStatus);

		pipelineStatusManager.updatePipelineStatus(newStatus);
		final var updatedStatus = pipelineStatusManager.getPipelineStatus();

		assertThat(updatedStatus)
				.isInstanceOf(currentStatus.getClass())
				.isNotInstanceOf(newStatus.getClass());
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

	static class IncompatibleStatusesProvider implements ArgumentsProvider {
		@Override
		public Stream<Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(
					Arguments.of(new InitPipelineStatus(), new StartedPipelineStatus()),
					Arguments.of(new RunningPipelineStatus(), new InitPipelineStatus()),
					Arguments.of(new StoppedPipelineStatus(), new InitPipelineStatus()),
					Arguments.of(new StoppedPipelineStatus(), new StartedPipelineStatus()),
					Arguments.of(new StoppedPipelineStatus(), new RunningPipelineStatus()),
					Arguments.of(new StoppedPipelineStatus(), new HaltedPipelineStatus()),
					Arguments.of(new InitPipelineStatus(), new HaltedPipelineStatus())
			);
		}
	}

}